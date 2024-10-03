package com.cashflow.repository;

import com.cashflow.model.Credit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Repository for Credit entity database operations
 */
@Repository
public interface CreditRepository extends JpaRepository<Credit, Long> {

    /**
     * Find all credits for a specific user
     */
    List<Credit> findByUserId(Long userId);

    /**
     * Find credits by user ordered by date descending
     */
    List<Credit> findByUserIdOrderByCreditDateDesc(Long userId);

    /**
     * Find credits by source
     */
    List<Credit> findByUserIdAndSource(Long userId, String source);

    /**
     * Find credits by credit type
     */
    List<Credit> findByUserIdAndCreditType(Long userId, String creditType);

    /**
     * Find credits within date range for user
     */
    List<Credit> findByUserIdAndCreditDateBetween(Long userId, LocalDate startDate, LocalDate endDate);

    /**
     * Calculate total credits for user
     */
    @Query("SELECT COALESCE(SUM(c.amount), 0) FROM Credit c WHERE c.user.id = :userId")
    BigDecimal getTotalCreditsByUserId(@Param("userId") Long userId);

    /**
     * Calculate total credits for user within date range
     */
    @Query("SELECT COALESCE(SUM(c.amount), 0) FROM Credit c WHERE c.user.id = :userId " +
            "AND c.creditDate BETWEEN :startDate AND :endDate")
    BigDecimal getTotalCreditsByUserIdAndDateRange(
            @Param("userId") Long userId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    /**
     * Count credits for user
     */
    long countByUserId(Long userId);

    /**
     * Get credit summary by type for user
     */
    @Query("SELECT c.creditType, SUM(c.amount), COUNT(c) FROM Credit c " +
            "WHERE c.user.id = :userId GROUP BY c.creditType")
    List<Object[]> getCreditSummaryByType(@Param("userId") Long userId);

    /**
     * Get credit summary by source for user
     */
    @Query("SELECT c.source, SUM(c.amount), COUNT(c) FROM Credit c " +
            "WHERE c.user.id = :userId GROUP BY c.source")
    List<Object[]> getCreditSummaryBySource(@Param("userId") Long userId);
}
// Credit summary queries
// Query optimization
