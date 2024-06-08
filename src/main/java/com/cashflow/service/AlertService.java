package com.cashflow.service;

import com.cashflow.dto.AlertDTO;
import com.cashflow.model.Threshold;
import com.cashflow.repository.ExpenseRepository;
import com.cashflow.repository.ThresholdRepository;
import com.cashflow.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Service class for Alert-related business logic
 * Handles threshold breach detection and alert generation
 */
@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class AlertService {

    private final ThresholdRepository thresholdRepository;
    private final ExpenseRepository expenseRepository;
    private final UserRepository userRepository;

    /**
     * Check all thresholds for a user and generate alerts
     */
    public List<AlertDTO> checkThresholdBreaches(Long userId) {
        List<AlertDTO> alerts = new ArrayList<>();
        List<Threshold> activeThresholds = thresholdRepository.findByUserIdAndIsActiveTrue(userId);

        for (Threshold threshold : activeThresholds) {
            AlertDTO alert = checkSingleThreshold(threshold);
            if (alert != null) {
                alerts.add(alert);
            }
        }

        return alerts;
    }

    /**
     * Get all breach alerts for a user
     */
    @Transactional(readOnly = true)
    public List<AlertDTO> getThresholdBreachedAlerts(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new com.cashflow.exception.ResourceNotFoundException("User", "id", userId);
        }

        List<AlertDTO> alerts = new ArrayList<>();
        List<Threshold> activeThresholds = thresholdRepository.findByUserIdAndIsActiveTrue(userId);

        for (Threshold threshold : activeThresholds) {
            BigDecimal currentSpending = calculateCurrentSpending(threshold);
            BigDecimal limitAmount = threshold.getLimitAmount();

            double usagePercentage = currentSpending.divide(limitAmount, 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100)).doubleValue();

            String categoryName = threshold.getCategory() != null ? threshold.getCategory().getName() : null;
            Long categoryId = threshold.getCategory() != null ? threshold.getCategory().getId() : null;
            String username = threshold.getUser().getUsername();

            // Generate alert if threshold is breached (100%+)
            if (usagePercentage >= 100) {
                alerts.add(AlertDTO.createBreachAlert(userId, username, categoryId, categoryName,
                        limitAmount, currentSpending));
            }
            // Generate warning alert if approaching threshold
            else if (usagePercentage >= threshold.getAlertPercentage()) {
                alerts.add(AlertDTO.createWarningAlert(userId, username, categoryId, categoryName,
                        limitAmount, currentSpending));
            }
        }

        return alerts;
    }

    /**
     * Check a single threshold and generate alert if necessary
     */
    private AlertDTO checkSingleThreshold(Threshold threshold) {
        BigDecimal currentSpending = calculateCurrentSpending(threshold);
        BigDecimal limitAmount = threshold.getLimitAmount();

        double usagePercentage = currentSpending.divide(limitAmount, 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100)).doubleValue();

        String categoryName = threshold.getCategory() != null ? threshold.getCategory().getName() : null;
        Long categoryId = threshold.getCategory() != null ? threshold.getCategory().getId() : null;
        String username = threshold.getUser().getUsername();
        Long userId = threshold.getUser().getId();

        // Check if threshold is breached
        if (usagePercentage >= 100) {
            // Update threshold breach status
            if (!threshold.getIsBreached()) {
                threshold.setIsBreached(true);
                threshold.setLastAlertSent(LocalDateTime.now());
                thresholdRepository.save(threshold);
                log.warn("Threshold breached for user {} - Category: {}, Limit: {}, Current: {}",
                        username, categoryName, limitAmount, currentSpending);
            }
            return AlertDTO.createBreachAlert(userId, username, categoryId, categoryName,
                    limitAmount, currentSpending);
        }
        // Check if warning threshold is reached
        else if (usagePercentage >= threshold.getAlertPercentage()) {
            log.info("Threshold warning for user {} - Category: {}, Usage: {}%",
                    username, categoryName, usagePercentage);
            return AlertDTO.createWarningAlert(userId, username, categoryId, categoryName,
                    limitAmount, currentSpending);
        }
        // Reset breach status if spending dropped below limit
        else if (threshold.getIsBreached()) {
            threshold.setIsBreached(false);
            thresholdRepository.save(threshold);
        }

        return null;
    }

    /**
     * Calculate current spending based on threshold type and category
     */
    private BigDecimal calculateCurrentSpending(Threshold threshold) {
        Long userId = threshold.getUser().getId();

        if (threshold.getCategory() != null) {
            return expenseRepository.getTotalExpensesByUserIdAndCategoryId(userId, threshold.getCategory().getId());
        } else {
            return expenseRepository.getTotalExpensesByUserId(userId);
        }
    }

    /**
     * Get count of breached thresholds for user
     */
    @Transactional(readOnly = true)
    public long getBreachedThresholdCount(Long userId) {
        return thresholdRepository.countByUserIdAndIsBreachedTrue(userId);
    }

    /**
     * Get all breached thresholds for user
     */
    @Transactional(readOnly = true)
    public List<Threshold> getBreachedThresholds(Long userId) {
        return thresholdRepository.findByUserIdAndIsBreachedTrue(userId);
    }
}
