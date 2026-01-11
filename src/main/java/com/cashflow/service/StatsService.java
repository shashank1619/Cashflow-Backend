package com.cashflow.service;

import com.cashflow.dto.MonthlyStatsDTO;
import com.cashflow.dto.MonthlyTrendDTO;
import com.cashflow.model.Expense;
import com.cashflow.repository.ExpenseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.Month;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service for generating expense statistics and analytics
 */
@Service
@RequiredArgsConstructor
public class StatsService {

        private final ExpenseRepository expenseRepository;

        // Predefined colors for pie chart categories
        private static final String[] CATEGORY_COLORS = {
                        "#6366f1", "#8b5cf6", "#ec4899", "#f43f5e", "#f97316",
                        "#eab308", "#22c55e", "#14b8a6", "#06b6d4", "#3b82f6"
        };

        /**
         * Get monthly statistics for a specific month
         */
        public MonthlyStatsDTO getMonthlyStats(Long userId, int year, int month) {
                YearMonth yearMonth = YearMonth.of(year, month);
                LocalDate startDate = yearMonth.atDay(1);
                LocalDate endDate = yearMonth.atEndOfMonth();

                // Get expenses for the selected month
                List<Expense> expenses = expenseRepository.findByUserIdAndExpenseDateBetween(
                                userId, startDate, endDate);

                // Calculate totals
                BigDecimal totalSpent = expenses.stream()
                                .map(Expense::getAmount)
                                .reduce(BigDecimal.ZERO, BigDecimal::add);

                int daysInMonth = yearMonth.lengthOfMonth();
                BigDecimal avgDaily = totalSpent.divide(
                                BigDecimal.valueOf(daysInMonth), 2, RoundingMode.HALF_UP);

                // Get previous month data for comparison
                YearMonth prevMonth = yearMonth.minusMonths(1);
                BigDecimal previousMonthTotal = expenseRepository.getTotalExpensesByUserIdAndDateRange(
                                userId, prevMonth.atDay(1), prevMonth.atEndOfMonth());
                if (previousMonthTotal == null)
                        previousMonthTotal = BigDecimal.ZERO;

                // Calculate change
                BigDecimal changeAmount = totalSpent.subtract(previousMonthTotal);
                Double changePercentage = 0.0;
                if (previousMonthTotal.compareTo(BigDecimal.ZERO) > 0) {
                        changePercentage = changeAmount
                                        .divide(previousMonthTotal, 4, RoundingMode.HALF_UP)
                                        .multiply(BigDecimal.valueOf(100))
                                        .doubleValue();
                }

                // Category breakdown
                List<MonthlyStatsDTO.CategoryBreakdown> categoryBreakdown = buildCategoryBreakdown(
                                expenses, totalSpent);

                // Find top category
                String topCategoryName = "-";
                BigDecimal topCategoryAmount = BigDecimal.ZERO;
                if (!categoryBreakdown.isEmpty()) {
                        MonthlyStatsDTO.CategoryBreakdown top = categoryBreakdown.get(0);
                        topCategoryName = top.getCategoryName();
                        topCategoryAmount = top.getAmount();
                }

                // Daily breakdown
                List<MonthlyStatsDTO.DailyBreakdown> dailyBreakdown = buildDailyBreakdown(
                                expenses, yearMonth);

                return MonthlyStatsDTO.builder()
                                .year(year)
                                .month(month)
                                .monthName(Month.of(month).getDisplayName(TextStyle.FULL, Locale.ENGLISH))
                                .totalSpent(totalSpent)
                                .avgDaily(avgDaily)
                                .transactionCount(expenses.size())
                                .daysInMonth(daysInMonth)
                                .previousMonthTotal(previousMonthTotal)
                                .changeAmount(changeAmount)
                                .changePercentage(changePercentage)
                                .isIncrease(changeAmount.compareTo(BigDecimal.ZERO) > 0)
                                .topCategoryName(topCategoryName)
                                .topCategoryAmount(topCategoryAmount)
                                .categoryBreakdown(categoryBreakdown)
                                .dailyBreakdown(dailyBreakdown)
                                .build();
        }

        /**
         * Get trend data for last N months
         */
        public List<MonthlyTrendDTO> getMonthlyTrends(Long userId, int months) {
                List<MonthlyTrendDTO> trends = new ArrayList<>();
                YearMonth current = YearMonth.now();

                for (int i = months - 1; i >= 0; i--) {
                        YearMonth ym = current.minusMonths(i);
                        LocalDate start = ym.atDay(1);
                        LocalDate end = ym.atEndOfMonth();

                        BigDecimal total = expenseRepository.getTotalExpensesByUserIdAndDateRange(
                                        userId, start, end);
                        if (total == null)
                                total = BigDecimal.ZERO;

                        List<Expense> monthExpenses = expenseRepository.findByUserIdAndExpenseDateBetween(
                                        userId, start, end);

                        trends.add(MonthlyTrendDTO.builder()
                                        .year(ym.getYear())
                                        .month(ym.getMonthValue())
                                        .monthName(ym.getMonth().getDisplayName(TextStyle.SHORT, Locale.ENGLISH))
                                        .totalSpent(total)
                                        .transactionCount(monthExpenses.size())
                                        .build());
                }

                return trends;
        }

        /**
         * Get trend data for last N months filtered by category
         */
        public List<MonthlyTrendDTO> getMonthlyTrendsByCategory(Long userId, int months, Long categoryId) {
                List<MonthlyTrendDTO> trends = new ArrayList<>();
                YearMonth current = YearMonth.now();

                for (int i = months - 1; i >= 0; i--) {
                        YearMonth ym = current.minusMonths(i);
                        LocalDate start = ym.atDay(1);
                        LocalDate end = ym.atEndOfMonth();

                        BigDecimal total = expenseRepository.getTotalExpensesByUserIdAndCategoryIdAndDateRange(
                                        userId, categoryId, start, end);
                        if (total == null)
                                total = BigDecimal.ZERO;

                        List<Expense> monthExpenses = expenseRepository.findByUserIdAndCategoryIdAndExpenseDateBetween(
                                        userId, categoryId, start, end);

                        trends.add(MonthlyTrendDTO.builder()
                                        .year(ym.getYear())
                                        .month(ym.getMonthValue())
                                        .monthName(ym.getMonth().getDisplayName(TextStyle.SHORT, Locale.ENGLISH))
                                        .totalSpent(total)
                                        .transactionCount(monthExpenses.size())
                                        .build());
                }

                return trends;
        }

        /**
         * Build category breakdown with percentages and colors
         */
        private List<MonthlyStatsDTO.CategoryBreakdown> buildCategoryBreakdown(
                        List<Expense> expenses, BigDecimal totalSpent) {

                if (expenses.isEmpty() || totalSpent.compareTo(BigDecimal.ZERO) == 0) {
                        return Collections.emptyList();
                }

                // Group by category
                Map<String, BigDecimal> categoryTotals = expenses.stream()
                                .collect(Collectors.groupingBy(
                                                e -> e.getCategory() != null ? e.getCategory().getName()
                                                                : "Uncategorized",
                                                Collectors.reducing(BigDecimal.ZERO, Expense::getAmount,
                                                                BigDecimal::add)));

                Map<String, Long> categoryIds = expenses.stream()
                                .filter(e -> e.getCategory() != null)
                                .collect(Collectors.toMap(
                                                e -> e.getCategory().getName(),
                                                e -> e.getCategory().getId(),
                                                (a, b) -> a));

                List<MonthlyStatsDTO.CategoryBreakdown> breakdown = new ArrayList<>();
                int colorIndex = 0;

                // Sort by amount descending
                List<Map.Entry<String, BigDecimal>> sorted = categoryTotals.entrySet().stream()
                                .sorted(Map.Entry.<String, BigDecimal>comparingByValue().reversed())
                                .collect(Collectors.toList());

                for (Map.Entry<String, BigDecimal> entry : sorted) {
                        Double percentage = entry.getValue()
                                        .divide(totalSpent, 4, RoundingMode.HALF_UP)
                                        .multiply(BigDecimal.valueOf(100))
                                        .doubleValue();

                        breakdown.add(MonthlyStatsDTO.CategoryBreakdown.builder()
                                        .categoryId(categoryIds.getOrDefault(entry.getKey(), 0L))
                                        .categoryName(entry.getKey())
                                        .amount(entry.getValue())
                                        .percentage(percentage)
                                        .color(CATEGORY_COLORS[colorIndex % CATEGORY_COLORS.length])
                                        .build());

                        colorIndex++;
                }

                return breakdown;
        }

        /**
         * Build daily breakdown for area chart
         */
        private List<MonthlyStatsDTO.DailyBreakdown> buildDailyBreakdown(
                        List<Expense> expenses, YearMonth yearMonth) {

                // Group expenses by day
                Map<Integer, BigDecimal> dailyTotals = expenses.stream()
                                .collect(Collectors.groupingBy(
                                                e -> e.getExpenseDate().getDayOfMonth(),
                                                Collectors.reducing(BigDecimal.ZERO, Expense::getAmount,
                                                                BigDecimal::add)));

                List<MonthlyStatsDTO.DailyBreakdown> breakdown = new ArrayList<>();

                // Fill in all days of the month
                for (int day = 1; day <= yearMonth.lengthOfMonth(); day++) {
                        LocalDate date = yearMonth.atDay(day);
                        breakdown.add(MonthlyStatsDTO.DailyBreakdown.builder()
                                        .day(day)
                                        .date(date.toString())
                                        .amount(dailyTotals.getOrDefault(day, BigDecimal.ZERO))
                                        .build());
                }

                return breakdown;
        }
}
