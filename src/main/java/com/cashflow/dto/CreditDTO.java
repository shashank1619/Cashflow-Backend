package com.cashflow.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Credit Data Transfer Object
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreditDTO {

    private Long id;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    private BigDecimal amount;

    private String source;

    private String description;

    private LocalDate creditDate;

    private String creditType;

    private Boolean isRecurring;

    private String recurringFrequency;

    @NotNull(message = "User ID is required")
    private Long userId;

    // Response fields
    private String username;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
