package com.cashflow.repository;

import com.cashflow.model.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Repository for Expense entity database operations
 */
@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    /**
     * Find all expenses for a specific user
     */
    List<Expense> findByUserId(Long userId);

    /**
     * Find expenses by user ordered by date descending
     */
    List<Expense> findByUserIdOrderByExpenseDateDesc(Long userId);

    /**
     * Find expenses by category
     */
    List<Expense> findByCategoryId(Long categoryId);

    /**
     * Find expenses by user and category
     */
    List<Expense> findByUserIdAndCategoryId(Long userId, Long categoryId);

    /**
     * Find expenses within date range for user
     */
    List<Expense> findByUserIdAndExpenseDateBetween(Long userId, LocalDate startDate, LocalDate endDate);

    /**
     * Find expenses within date range for user and category
     */
    List<Expense> findByUserIdAndCategoryIdAndExpenseDateBetween(
            Long userId, Long categoryId, LocalDate startDate, LocalDate endDate);

    /**
     * Calculate total expenses for user
     */
    @Query("SELECT COALESCE(SUM(e.amount), 0) FROM Expense e WHERE e.user.id = :userId")
    BigDecimal getTotalExpensesByUserId(@Param("userId") Long userId);

    /**
     * Calculate total expenses for user within date range
     */
    @Query("SELECT COALESCE(SUM(e.amount), 0) FROM Expense e WHERE e.user.id = :userId " +
            "AND e.expenseDate BETWEEN :startDate AND :endDate")
    BigDecimal getTotalExpensesByUserIdAndDateRange(
            @Param("userId") Long userId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    /**
     * Calculate total expenses for user by category
     */
    @Query("SELECT COALESCE(SUM(e.amount), 0) FROM Expense e WHERE e.user.id = :userId AND e.category.id = :categoryId")
    BigDecimal getTotalExpensesByUserIdAndCategoryId(
            @Param("userId") Long userId, @Param("categoryId") Long categoryId);

    /**
     * Calculate total expenses for user by category within date range
     */
    @Query("SELECT COALESCE(SUM(e.amount), 0) FROM Expense e WHERE e.user.id = :userId " +
            "AND e.category.id = :categoryId AND e.expenseDate BETWEEN :startDate AND :endDate")
    BigDecimal getTotalExpensesByUserIdAndCategoryIdAndDateRange(
            @Param("userId") Long userId,
            @Param("categoryId") Long categoryId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    /**
     * Count expenses for user
     */
    long countByUserId(Long userId);

    /**
     * Count expenses by category
     */
    long countByCategoryId(Long categoryId);

    /**
     * Get expense summary by category for user
     */
    @Query("SELECT e.category.id, e.category.name, SUM(e.amount), COUNT(e) " +
            "FROM Expense e WHERE e.user.id = :userId GROUP BY e.category.id, e.category.name")
    List<Object[]> getExpenseSummaryByCategory(@Param("userId") Long userId);

    /**
     * Get expense summary by category for user within date range
     */
    @Query("SELECT e.category.id, e.category.name, SUM(e.amount), COUNT(e) " +
            "FROM Expense e WHERE e.user.id = :userId AND e.expenseDate BETWEEN :startDate AND :endDate " +
            "GROUP BY e.category.id, e.category.name")
    List<Object[]> getExpenseSummaryByCategoryAndDateRange(
            @Param("userId") Long userId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);
}
