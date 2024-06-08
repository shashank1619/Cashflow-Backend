package com.cashflow.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * Expense Summary DTO for aggregated expense data
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExpenseSummaryDTO {

    private Long userId;
    private String username;
    private BigDecimal totalExpenses;
    private BigDecimal totalCredits;
    private BigDecimal netBalance;
    private Integer expenseCount;
    private Integer creditCount;
    private String period; // DAILY, WEEKLY, MONTHLY, YEARLY, ALL_TIME
    private List<CategoryExpenseDTO> categoryBreakdown;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CategoryExpenseDTO {
        private Long categoryId;
        private String categoryName;
        private BigDecimal totalAmount;
        private Integer count;
        private Double percentage;
    }
}
