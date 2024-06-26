package com.cashflow.repository;

import com.cashflow.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for Category entity database operations
 */
@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    /**
     * Find all categories for a specific user
     */
    List<Category> findByUserId(Long userId);

    /**
     * Find category by name and user
     */
    Optional<Category> findByNameAndUserId(String name, Long userId);

    /**
     * Check if category name exists for user
     */
    boolean existsByNameAndUserId(String name, Long userId);

    /**
     * Find default categories for user
     */
    List<Category> findByUserIdAndIsDefaultTrue(Long userId);

    /**
     * Count categories for user
     */
    long countByUserId(Long userId);

    /**
     * Find categories with expense count
     */
    @Query("SELECT c FROM Category c LEFT JOIN FETCH c.expenses WHERE c.user.id = :userId")
    List<Category> findByUserIdWithExpenses(@Param("userId") Long userId);

    /**
     * Search categories by name
     */
    @Query("SELECT c FROM Category c WHERE c.user.id = :userId AND LOWER(c.name) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<Category> searchCategories(@Param("userId") Long userId, @Param("searchTerm") String searchTerm);
}
// Category search
