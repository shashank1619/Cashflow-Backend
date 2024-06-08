package com.cashflow.service;

import com.cashflow.dto.ExpenseDTO;
import com.cashflow.dto.ExpenseSummaryDTO;
import com.cashflow.exception.ResourceNotFoundException;
import com.cashflow.model.Category;
import com.cashflow.model.Expense;
import com.cashflow.model.User;
import com.cashflow.repository.CategoryRepository;
import com.cashflow.repository.CreditRepository;
import com.cashflow.repository.ExpenseRepository;
import com.cashflow.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service class for Expense-related business logic
 */
@Service
@RequiredArgsConstructor
@Transactional
public class ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final CreditRepository creditRepository;
    private final AlertService alertService;

    /**
     * Add a new expense
     */
    public ExpenseDTO addExpense(ExpenseDTO expenseDTO) {
        User user = userRepository.findById(expenseDTO.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", expenseDTO.getUserId()));

        Category category = categoryRepository.findById(expenseDTO.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", expenseDTO.getCategoryId()));

        Expense expense = Expense.builder()
                .amount(expenseDTO.getAmount())
                .description(expenseDTO.getDescription())
                .expenseDate(expenseDTO.getExpenseDate() != null ? expenseDTO.getExpenseDate() : LocalDate.now())
                .paymentMethod(expenseDTO.getPaymentMethod())
                .merchantName(expenseDTO.getMerchantName())
                .isRecurring(expenseDTO.getIsRecurring() != null ? expenseDTO.getIsRecurring() : false)
                .recurringFrequency(expenseDTO.getRecurringFrequency())
                .user(user)
                .category(category)
                .build();

        Expense savedExpense = expenseRepository.save(expense);

        // Check for threshold breaches after adding expense
        alertService.checkThresholdBreaches(user.getId());

        return mapToDTO(savedExpense);
    }

    /**
     * Get expense by ID
     */
    @Transactional(readOnly = true)
    public ExpenseDTO getExpenseById(Long id) {
        Expense expense = expenseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Expense", "id", id));
        return mapToDTO(expense);
    }

    /**
     * Get all expenses for a user
     */
    @Transactional(readOnly = true)
    public List<ExpenseDTO> getExpensesByUserId(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User", "id", userId);
        }
        return expenseRepository.findByUserIdOrderByExpenseDateDesc(userId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get expenses by category
     */
    @Transactional(readOnly = true)
    public List<ExpenseDTO> getExpensesByCategory(Long userId, Long categoryId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User", "id", userId);
        }
        if (!categoryRepository.existsById(categoryId)) {
            throw new ResourceNotFoundException("Category", "id", categoryId);
        }
        return expenseRepository.findByUserIdAndCategoryId(userId, categoryId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get user's overall expense summary
     */
    @Transactional(readOnly = true)
    public ExpenseSummaryDTO getUserOverallExpense(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        BigDecimal totalExpenses = expenseRepository.getTotalExpensesByUserId(userId);
        BigDecimal totalCredits = creditRepository.getTotalCreditsByUserId(userId);
        BigDecimal netBalance = totalCredits.subtract(totalExpenses);

        long expenseCount = expenseRepository.countByUserId(userId);
        long creditCount = creditRepository.countByUserId(userId);

        // Get category breakdown
        List<Object[]> categoryData = expenseRepository.getExpenseSummaryByCategory(userId);
        List<ExpenseSummaryDTO.CategoryExpenseDTO> categoryBreakdown = new ArrayList<>();

        for (Object[] data : categoryData) {
            Long categoryId = (Long) data[0];
            String categoryName = (String) data[1];
            BigDecimal categoryTotal = (BigDecimal) data[2];
            Long count = (Long) data[3];

            double percentage = totalExpenses.compareTo(BigDecimal.ZERO) > 0
                    ? categoryTotal.divide(totalExpenses, 4, RoundingMode.HALF_UP)
                            .multiply(BigDecimal.valueOf(100)).doubleValue()
                    : 0.0;

            categoryBreakdown.add(ExpenseSummaryDTO.CategoryExpenseDTO.builder()
                    .categoryId(categoryId)
                    .categoryName(categoryName)
                    .totalAmount(categoryTotal)
                    .count(count.intValue())
                    .percentage(percentage)
                    .build());
        }

        return ExpenseSummaryDTO.builder()
                .userId(userId)
                .username(user.getUsername())
                .totalExpenses(totalExpenses)
                .totalCredits(totalCredits)
                .netBalance(netBalance)
                .expenseCount((int) expenseCount)
                .creditCount((int) creditCount)
                .period("ALL_TIME")
                .categoryBreakdown(categoryBreakdown)
                .build();
    }

    /**
     * Get expense summary for date range
     */
    @Transactional(readOnly = true)
    public ExpenseSummaryDTO getExpenseSummaryByDateRange(Long userId, LocalDate startDate, LocalDate endDate) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        BigDecimal totalExpenses = expenseRepository.getTotalExpensesByUserIdAndDateRange(userId, startDate, endDate);
        BigDecimal totalCredits = creditRepository.getTotalCreditsByUserIdAndDateRange(userId, startDate, endDate);
        BigDecimal netBalance = totalCredits.subtract(totalExpenses);

        List<Expense> expenses = expenseRepository.findByUserIdAndExpenseDateBetween(userId, startDate, endDate);
        int expenseCount = expenses.size();

        // Get category breakdown for date range
        List<Object[]> categoryData = expenseRepository.getExpenseSummaryByCategoryAndDateRange(userId, startDate,
                endDate);
        List<ExpenseSummaryDTO.CategoryExpenseDTO> categoryBreakdown = new ArrayList<>();

        for (Object[] data : categoryData) {
            Long categoryId = (Long) data[0];
            String categoryName = (String) data[1];
            BigDecimal categoryTotal = (BigDecimal) data[2];
            Long count = (Long) data[3];

            double percentage = totalExpenses.compareTo(BigDecimal.ZERO) > 0
                    ? categoryTotal.divide(totalExpenses, 4, RoundingMode.HALF_UP)
                            .multiply(BigDecimal.valueOf(100)).doubleValue()
                    : 0.0;

            categoryBreakdown.add(ExpenseSummaryDTO.CategoryExpenseDTO.builder()
                    .categoryId(categoryId)
                    .categoryName(categoryName)
                    .totalAmount(categoryTotal)
                    .count(count.intValue())
                    .percentage(percentage)
                    .build());
        }

        return ExpenseSummaryDTO.builder()
                .userId(userId)
                .username(user.getUsername())
                .totalExpenses(totalExpenses)
                .totalCredits(totalCredits)
                .netBalance(netBalance)
                .expenseCount(expenseCount)
                .period(startDate + " to " + endDate)
                .categoryBreakdown(categoryBreakdown)
                .build();
    }

    /**
     * Update expense
     */
    public ExpenseDTO updateExpense(Long id, ExpenseDTO expenseDTO) {
        Expense expense = expenseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Expense", "id", id));

        if (expenseDTO.getAmount() != null)
            expense.setAmount(expenseDTO.getAmount());
        if (expenseDTO.getDescription() != null)
            expense.setDescription(expenseDTO.getDescription());
        if (expenseDTO.getExpenseDate() != null)
            expense.setExpenseDate(expenseDTO.getExpenseDate());
        if (expenseDTO.getPaymentMethod() != null)
            expense.setPaymentMethod(expenseDTO.getPaymentMethod());
        if (expenseDTO.getMerchantName() != null)
            expense.setMerchantName(expenseDTO.getMerchantName());

        if (expenseDTO.getCategoryId() != null) {
            Category category = categoryRepository.findById(expenseDTO.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category", "id", expenseDTO.getCategoryId()));
            expense.setCategory(category);
        }

        Expense updatedExpense = expenseRepository.save(expense);

        // Check for threshold breaches
        alertService.checkThresholdBreaches(expense.getUser().getId());

        return mapToDTO(updatedExpense);
    }

    /**
     * Delete expense
     */
    public void deleteExpense(Long id) {
        Expense expense = expenseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Expense", "id", id));
        expenseRepository.delete(expense);
    }

    /**
     * Map Expense entity to DTO
     */
    private ExpenseDTO mapToDTO(Expense expense) {
        return ExpenseDTO.builder()
                .id(expense.getId())
                .amount(expense.getAmount())
                .description(expense.getDescription())
                .expenseDate(expense.getExpenseDate())
                .paymentMethod(expense.getPaymentMethod())
                .merchantName(expense.getMerchantName())
                .isRecurring(expense.getIsRecurring())
                .recurringFrequency(expense.getRecurringFrequency())
                .userId(expense.getUser().getId())
                .categoryId(expense.getCategory().getId())
                .categoryName(expense.getCategory().getName())
                .username(expense.getUser().getUsername())
                .createdAt(expense.getCreatedAt())
                .updatedAt(expense.getUpdatedAt())
                .build();
    }
}
