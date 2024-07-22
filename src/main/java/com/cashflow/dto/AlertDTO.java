package com.cashflow.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Alert Data Transfer Object for threshold breach notifications
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AlertDTO {

    private Long id;
    private Long userId;
    private String username;
    private Long thresholdId;
    private Long categoryId;
    private String categoryName;
    private String alertType; // WARNING, BREACH
    private String message;
    private BigDecimal limitAmount;
    private BigDecimal currentSpending;
    private Double usagePercentage;
    private Boolean isRead;
    private LocalDateTime createdAt;

    public static AlertDTO createWarningAlert(Long userId, String username,
            Long categoryId, String categoryName,
            BigDecimal limitAmount, BigDecimal currentSpending) {
        double percentage = currentSpending.divide(limitAmount, 4, java.math.RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100)).doubleValue();

        String message = categoryName != null
                ? String.format("Warning: You have reached %.1f%% of your %s expense limit", percentage, categoryName)
                : String.format("Warning: You have reached %.1f%% of your overall expense limit", percentage);

        return AlertDTO.builder()
                .userId(userId)
                .username(username)
                .categoryId(categoryId)
                .categoryName(categoryName)
                .alertType("WARNING")
                .message(message)
                .limitAmount(limitAmount)
                .currentSpending(currentSpending)
                .usagePercentage(percentage)
                .isRead(false)
                .createdAt(LocalDateTime.now())
                .build();
    }

    public static AlertDTO createBreachAlert(Long userId, String username,
            Long categoryId, String categoryName,
            BigDecimal limitAmount, BigDecimal currentSpending) {
        double percentage = currentSpending.divide(limitAmount, 4, java.math.RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100)).doubleValue();

        String message = categoryName != null
                ? String.format("ALERT: You have exceeded your %s expense limit by %.1f%%",
                        categoryName, percentage - 100)
                : String.format("ALERT: You have exceeded your overall expense limit by %.1f%%",
                        percentage - 100);

        return AlertDTO.builder()
                .userId(userId)
                .username(username)
                .categoryId(categoryId)
                .categoryName(categoryName)
                .alertType("BREACH")
                .message(message)
                .limitAmount(limitAmount)
                .currentSpending(currentSpending)
                .usagePercentage(percentage)
                .isRead(false)
                .createdAt(LocalDateTime.now())
                .build();
    }
}
// Alert message formatting
