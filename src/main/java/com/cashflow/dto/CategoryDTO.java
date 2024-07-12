package com.cashflow.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Category Data Transfer Object
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryDTO {

    private Long id;

    @NotBlank(message = "Category name is required")
    @Size(min = 2, max = 50, message = "Category name must be between 2 and 50 characters")
    private String name;

    private String description;

    private String iconName;

    private String colorCode;

    private Boolean isDefault;

    @NotNull(message = "User ID is required")
    private Long userId;

    // Response fields
    private String username;
    private Integer expenseCount;
    private BigDecimal totalExpenseAmount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
// Category response
