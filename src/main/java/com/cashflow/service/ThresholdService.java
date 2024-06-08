package com.cashflow.service;

import com.cashflow.dto.ThresholdDTO;
import com.cashflow.exception.DuplicateResourceException;
import com.cashflow.exception.ResourceNotFoundException;
import com.cashflow.model.Category;
import com.cashflow.model.Threshold;
import com.cashflow.model.User;
import com.cashflow.repository.CategoryRepository;
import com.cashflow.repository.ExpenseRepository;
import com.cashflow.repository.ThresholdRepository;
import com.cashflow.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service class for Threshold-related business logic
 */
@Service
@RequiredArgsConstructor
@Transactional
public class ThresholdService {

    private final ThresholdRepository thresholdRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final ExpenseRepository expenseRepository;

    /**
     * Set a new threshold
     */
    public ThresholdDTO setThreshold(ThresholdDTO thresholdDTO) {
        User user = userRepository.findById(thresholdDTO.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", thresholdDTO.getUserId()));

        Category category = null;
        if (thresholdDTO.getCategoryId() != null) {
            category = categoryRepository.findById(thresholdDTO.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category", "id", thresholdDTO.getCategoryId()));

            // Check for existing threshold for this category
            if (thresholdRepository.existsByUserIdAndCategoryId(user.getId(), category.getId())) {
                throw new DuplicateResourceException("Threshold already exists for this category");
            }
        } else {
            // Check for existing overall threshold
            if (thresholdRepository.existsOverallThresholdByUserId(user.getId())) {
                throw new DuplicateResourceException("Overall threshold already exists for this user");
            }
        }

        Threshold threshold = Threshold.builder()
                .limitAmount(thresholdDTO.getLimitAmount())
                .thresholdType(thresholdDTO.getThresholdType() != null ? thresholdDTO.getThresholdType() : "MONTHLY")
                .alertPercentage(thresholdDTO.getAlertPercentage() != null ? thresholdDTO.getAlertPercentage() : 80)
                .isActive(true)
                .isBreached(false)
                .user(user)
                .category(category)
                .build();

        Threshold savedThreshold = thresholdRepository.save(threshold);
        return mapToDTO(savedThreshold);
    }

    /**
     * Get threshold by ID
     */
    @Transactional(readOnly = true)
    public ThresholdDTO getThresholdById(Long id) {
        Threshold threshold = thresholdRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Threshold", "id", id));
        return mapToDTO(threshold);
    }

    /**
     * Get all thresholds for a user
     */
    @Transactional(readOnly = true)
    public List<ThresholdDTO> getThresholdsByUserId(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User", "id", userId);
        }
        return thresholdRepository.findByUserId(userId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get active thresholds for a user
     */
    @Transactional(readOnly = true)
    public List<ThresholdDTO> getActiveThresholds(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User", "id", userId);
        }
        return thresholdRepository.findByUserIdAndIsActiveTrue(userId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get overall threshold for user
     */
    @Transactional(readOnly = true)
    public ThresholdDTO getOverallThreshold(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User", "id", userId);
        }
        Threshold threshold = thresholdRepository.findOverallThresholdByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Overall threshold not found for user"));
        return mapToDTO(threshold);
    }

    /**
     * Update threshold
     */
    public ThresholdDTO updateThreshold(Long id, ThresholdDTO thresholdDTO) {
        Threshold threshold = thresholdRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Threshold", "id", id));

        if (thresholdDTO.getLimitAmount() != null)
            threshold.setLimitAmount(thresholdDTO.getLimitAmount());
        if (thresholdDTO.getThresholdType() != null)
            threshold.setThresholdType(thresholdDTO.getThresholdType());
        if (thresholdDTO.getAlertPercentage() != null)
            threshold.setAlertPercentage(thresholdDTO.getAlertPercentage());
        if (thresholdDTO.getIsActive() != null)
            threshold.setIsActive(thresholdDTO.getIsActive());

        Threshold updatedThreshold = thresholdRepository.save(threshold);
        return mapToDTO(updatedThreshold);
    }

    /**
     * Delete threshold
     */
    public void deleteThreshold(Long id) {
        Threshold threshold = thresholdRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Threshold", "id", id));
        thresholdRepository.delete(threshold);
    }

    /**
     * Toggle threshold active status
     */
    public ThresholdDTO toggleThresholdStatus(Long id) {
        Threshold threshold = thresholdRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Threshold", "id", id));
        threshold.setIsActive(!threshold.getIsActive());
        Threshold updatedThreshold = thresholdRepository.save(threshold);
        return mapToDTO(updatedThreshold);
    }

    /**
     * Map Threshold entity to DTO with current spending info
     */
    private ThresholdDTO mapToDTO(Threshold threshold) {
        BigDecimal currentSpending;
        if (threshold.getCategory() != null) {
            currentSpending = expenseRepository.getTotalExpensesByUserIdAndCategoryId(
                    threshold.getUser().getId(), threshold.getCategory().getId());
        } else {
            currentSpending = expenseRepository.getTotalExpensesByUserId(threshold.getUser().getId());
        }

        BigDecimal remainingAmount = threshold.getLimitAmount().subtract(currentSpending);
        double usagePercentage = currentSpending.divide(threshold.getLimitAmount(), 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100)).doubleValue();

        return ThresholdDTO.builder()
                .id(threshold.getId())
                .limitAmount(threshold.getLimitAmount())
                .thresholdType(threshold.getThresholdType())
                .alertPercentage(threshold.getAlertPercentage())
                .isActive(threshold.getIsActive())
                .isBreached(threshold.getIsBreached())
                .userId(threshold.getUser().getId())
                .username(threshold.getUser().getUsername())
                .categoryId(threshold.getCategory() != null ? threshold.getCategory().getId() : null)
                .categoryName(threshold.getCategory() != null ? threshold.getCategory().getName() : null)
                .currentSpending(currentSpending)
                .remainingAmount(remainingAmount)
                .usagePercentage(usagePercentage)
                .lastAlertSent(threshold.getLastAlertSent())
                .createdAt(threshold.getCreatedAt())
                .updatedAt(threshold.getUpdatedAt())
                .build();
    }
}
