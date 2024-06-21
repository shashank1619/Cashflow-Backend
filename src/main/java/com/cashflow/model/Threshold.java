package com.cashflow.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Threshold Entity - Represents expense limits set by users for alerts
 */
@Entity
@Table(name = "thresholds")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Threshold {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Limit amount is required")
    @DecimalMin(value = "0.01", message = "Limit amount must be greater than 0")
    @Column(name = "limit_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal limitAmount;

    @Column(name = "threshold_type")
    @Builder.Default
    private String thresholdType = "MONTHLY"; // DAILY, WEEKLY, MONTHLY, YEARLY

    @Column(name = "alert_percentage")
    @Builder.Default
    private Integer alertPercentage = 80; // Alert when reaching this percentage

    @Column(name = "is_active")
    @Builder.Default
    private Boolean isActive = true;

    @Column(name = "is_breached")
    @Builder.Default
    private Boolean isBreached = false;

    @Column(name = "last_alert_sent")
    private LocalDateTime lastAlertSent;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Many-to-One relationship with User
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Many-to-One relationship with Category (optional - can be overall threshold)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    /**
     * Check if this is an overall threshold (not category-specific)
     */
    public boolean isOverallThreshold() {
        return category == null;
    }
}
// Threshold percentage calc
