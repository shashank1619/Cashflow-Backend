package com.cashflow.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Threshold Data Transfer Object
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ThresholdDTO {

    private Long id;

    @NotNull(message = "Limit amount is required")
    @DecimalMin(value = "0.01", message = "Limit amount must be greater than 0")
    private BigDecimal limitAmount;

    private String thresholdType; // DAILY, WEEKLY, MONTHLY, YEARLY

    private Integer alertPercentage;

    private Boolean isActive;

    private Boolean isBreached;

    @NotNull(message = "User ID is required")
    private Long userId;

    private Long categoryId; // Optional - null for overall threshold

    // Response fields
    private String username;
    private String categoryName;
    private BigDecimal currentSpending;
    private BigDecimal remainingAmount;
    private Double usagePercentage;
    private LocalDateTime lastAlertSent;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
// Threshold usage calc
