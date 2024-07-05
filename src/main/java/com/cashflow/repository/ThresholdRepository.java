package com.cashflow.repository;

import com.cashflow.model.Threshold;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for Threshold entity database operations
 */
@Repository
public interface ThresholdRepository extends JpaRepository<Threshold, Long> {

    /**
     * Find all thresholds for a specific user
     */
    List<Threshold> findByUserId(Long userId);

    /**
     * Find active thresholds for user
     */
    List<Threshold> findByUserIdAndIsActiveTrue(Long userId);

    /**
     * Find threshold by user and category
     */
    Optional<Threshold> findByUserIdAndCategoryId(Long userId, Long categoryId);

    /**
     * Find overall threshold for user (no category)
     */
    @Query("SELECT t FROM Threshold t WHERE t.user.id = :userId AND t.category IS NULL")
    Optional<Threshold> findOverallThresholdByUserId(@Param("userId") Long userId);

    /**
     * Find breached thresholds for user
     */
    List<Threshold> findByUserIdAndIsBreachedTrue(Long userId);

    /**
     * Find active breached thresholds for user
     */
    List<Threshold> findByUserIdAndIsActiveTrueAndIsBreachedTrue(Long userId);

    /**
     * Find category-specific thresholds for user
     */
    @Query("SELECT t FROM Threshold t WHERE t.user.id = :userId AND t.category IS NOT NULL")
    List<Threshold> findCategoryThresholdsByUserId(@Param("userId") Long userId);

    /**
     * Check if threshold exists for user and category
     */
    boolean existsByUserIdAndCategoryId(Long userId, Long categoryId);

    /**
     * Check if overall threshold exists for user
     */
    @Query("SELECT COUNT(t) > 0 FROM Threshold t WHERE t.user.id = :userId AND t.category IS NULL")
    boolean existsOverallThresholdByUserId(@Param("userId") Long userId);

    /**
     * Count thresholds for user
     */
    long countByUserId(Long userId);

    /**
     * Count breached thresholds for user
     */
    long countByUserIdAndIsBreachedTrue(Long userId);
}
// Threshold breach detection
