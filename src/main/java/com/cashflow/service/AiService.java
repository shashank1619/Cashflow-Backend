package com.cashflow.service;

import com.cashflow.model.Expense;
import com.cashflow.repository.ExpenseRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service for AI-powered expense analysis using Google Gemini
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AiService {

    private final ExpenseRepository expenseRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final HttpClient httpClient = HttpClient.newHttpClient();

    @Value("${gemini.api.key}")
    private String geminiApiKey;

    @Value("${gemini.api.url}")
    private String geminiApiUrl;

    /**
     * Chat with AI about expenses
     */
    public String chat(Long userId, String userMessage) {
        try {
            // Build context from user's expense data
            String expenseContext = buildExpenseContext(userId);

            // Create the prompt
            String prompt = buildPrompt(expenseContext, userMessage);

            // Call Gemini API
            return callGeminiApi(prompt);
        } catch (Exception e) {
            log.error("Error in AI chat: ", e);
            return "I'm sorry, I encountered an error processing your request. Please try again.";
        }
    }

    /**
     * Build context from user's expense data
     */
    private String buildExpenseContext(Long userId) {
        YearMonth currentMonth = YearMonth.now();
        YearMonth lastMonth = currentMonth.minusMonths(1);

        // Get current month expenses
        List<Expense> currentMonthExpenses = expenseRepository.findByUserIdAndExpenseDateBetween(
                userId, currentMonth.atDay(1), currentMonth.atEndOfMonth());

        // Get last month expenses
        List<Expense> lastMonthExpenses = expenseRepository.findByUserIdAndExpenseDateBetween(
                userId, lastMonth.atDay(1), lastMonth.atEndOfMonth());

        // Calculate totals
        BigDecimal currentTotal = currentMonthExpenses.stream()
                .map(Expense::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal lastMonthTotal = lastMonthExpenses.stream()
                .map(Expense::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Category breakdown for current month
        Map<String, BigDecimal> categoryTotals = currentMonthExpenses.stream()
                .collect(Collectors.groupingBy(
                        e -> e.getCategory() != null ? e.getCategory().getName() : "Uncategorized",
                        Collectors.reducing(BigDecimal.ZERO, Expense::getAmount, BigDecimal::add)));

        // Build context string
        StringBuilder context = new StringBuilder();
        context.append("=== USER'S EXPENSE DATA ===\n\n");
        context.append("Current Month (").append(currentMonth.getMonth()).append(" ").append(currentMonth.getYear())
                .append("):\n");
        context.append("- Total Spent: ₹").append(currentTotal).append("\n");
        context.append("- Number of Transactions: ").append(currentMonthExpenses.size()).append("\n");
        context.append("- Daily Average: ₹").append(
                currentTotal.divide(BigDecimal.valueOf(Math.max(1, LocalDate.now().getDayOfMonth())), 2,
                        java.math.RoundingMode.HALF_UP))
                .append("\n\n");

        context.append("Category Breakdown:\n");
        categoryTotals.entrySet().stream()
                .sorted((a, b) -> b.getValue().compareTo(a.getValue()))
                .forEach(e -> context.append("- ").append(e.getKey()).append(": ₹").append(e.getValue()).append("\n"));

        context.append("\nLast Month (").append(lastMonth.getMonth()).append(" ").append(lastMonth.getYear())
                .append("):\n");
        context.append("- Total Spent: ₹").append(lastMonthTotal).append("\n");
        context.append("- Number of Transactions: ").append(lastMonthExpenses.size()).append("\n\n");

        // Recent transactions
        context.append("Recent Transactions (last 5):\n");
        currentMonthExpenses.stream()
                .sorted((a, b) -> b.getExpenseDate().compareTo(a.getExpenseDate()))
                .limit(5)
                .forEach(e -> context.append("- ").append(e.getExpenseDate()).append(": ")
                        .append(e.getDescription()).append(" - ₹").append(e.getAmount())
                        .append(" (").append(e.getCategory() != null ? e.getCategory().getName() : "Uncategorized")
                        .append(")\n"));

        return context.toString();
    }

    /**
     * Build the prompt for Gemini
     */
    private String buildPrompt(String expenseContext, String userMessage) {
        return """
                You are a helpful AI financial assistant for a personal expense tracking app called Cashflow.
                Your job is to analyze the user's expense data and provide helpful, personalized advice.

                Be conversational, friendly, and use emojis sparingly to make responses engaging.
                Keep responses concise but informative. Use bullet points for lists.
                Always reference actual numbers from the user's data when answering questions.
                If asked about savings tips, provide specific, actionable advice based on their spending patterns.

                %s

                User's Question: %s

                Please provide a helpful response based on the user's expense data above.
                """.formatted(expenseContext, userMessage);
    }

    /**
     * Call the Gemini API
     */
    private String callGeminiApi(String prompt) throws Exception {
        String requestBody = objectMapper.writeValueAsString(Map.of(
                "contents", List.of(Map.of(
                        "parts", List.of(Map.of("text", prompt)))),
                "generationConfig", Map.of(
                        "temperature", 0.7,
                        "maxOutputTokens", 500)));

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(geminiApiUrl + "?key=" + geminiApiKey))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            log.error("Gemini API error: {} - {}", response.statusCode(), response.body());
            throw new RuntimeException("Gemini API returned error: " + response.statusCode());
        }

        // Parse response
        JsonNode root = objectMapper.readTree(response.body());
        JsonNode candidates = root.path("candidates");
        if (candidates.isArray() && candidates.size() > 0) {
            return candidates.get(0).path("content").path("parts").get(0).path("text").asText();
        }

        throw new RuntimeException("Unexpected Gemini API response format");
    }
}
