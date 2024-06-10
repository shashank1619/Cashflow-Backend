package com.cashflow.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.math.BigDecimal;

/**
 * Exception thrown when an expense threshold is breached
 */
@ResponseStatus(HttpStatus.OK) // Returns 200 with alert info
public class ThresholdBreachedException extends RuntimeException {

    private Long userId;
    private Long categoryId;
    private String categoryName;
    private BigDecimal limitAmount;
    private BigDecimal currentSpending;
    private Double usagePercentage;

    public ThresholdBreachedException(Long userId, Long categoryId, String categoryName,
            BigDecimal limitAmount, BigDecimal currentSpending) {
        super(generateMessage(categoryName, currentSpending, limitAmount));
        this.userId = userId;
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.limitAmount = limitAmount;
        this.currentSpending = currentSpending;
        this.usagePercentage = calculatePercentage(currentSpending, limitAmount);
    }

    private static String generateMessage(String categoryName, BigDecimal currentSpending, BigDecimal limitAmount) {
        double percentage = calculatePercentage(currentSpending, limitAmount);
        if (categoryName != null) {
            return String.format(
                    "Threshold breached for category '%s': Current spending (%.2f) exceeds limit (%.2f) by %.1f%%",
                    categoryName, currentSpending, limitAmount, percentage - 100);
        }
        return String.format("Overall threshold breached: Current spending (%.2f) exceeds limit (%.2f) by %.1f%%",
                currentSpending, limitAmount, percentage - 100);
    }

    private static double calculatePercentage(BigDecimal currentSpending, BigDecimal limitAmount) {
        return currentSpending.divide(limitAmount, 4, java.math.RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100)).doubleValue();
    }

    public Long getUserId() {
        return userId;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public BigDecimal getLimitAmount() {
        return limitAmount;
    }

    public BigDecimal getCurrentSpending() {
        return currentSpending;
    }

    public Double getUsagePercentage() {
        return usagePercentage;
    }
}
