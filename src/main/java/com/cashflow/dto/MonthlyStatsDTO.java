package com.cashflow.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * DTO for Monthly Statistics data
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MonthlyStatsDTO {

    private int year;
    private int month;
    private String monthName;

    // Summary metrics
    private BigDecimal totalSpent;
    private BigDecimal avgDaily;
    private int transactionCount;
    private int daysInMonth;

    // Comparison with previous month
    private BigDecimal previousMonthTotal;
    private BigDecimal changeAmount;
    private Double changePercentage;
    private boolean isIncrease;

    // Top category
    private String topCategoryName;
    private BigDecimal topCategoryAmount;

    // Category breakdown for pie chart
    private List<CategoryBreakdown> categoryBreakdown;

    // Daily breakdown for area chart
    private List<DailyBreakdown> dailyBreakdown;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CategoryBreakdown {
        private Long categoryId;
        private String categoryName;
        private BigDecimal amount;
        private Double percentage;
        private String color;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DailyBreakdown {
        private int day;
        private String date;
        private BigDecimal amount;
    }
}
