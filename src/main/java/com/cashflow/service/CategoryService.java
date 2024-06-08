package com.cashflow.service;

import com.cashflow.dto.CategoryDTO;
import com.cashflow.exception.DuplicateResourceException;
import com.cashflow.exception.ResourceNotFoundException;
import com.cashflow.model.Category;
import com.cashflow.model.User;
import com.cashflow.repository.CategoryRepository;
import com.cashflow.repository.ExpenseRepository;
import com.cashflow.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service class for Category-related business logic
 */
@Service
@RequiredArgsConstructor
@Transactional
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final ExpenseRepository expenseRepository;

    /**
     * Create a new category
     */
    public CategoryDTO createCategory(CategoryDTO categoryDTO) {
        User user = userRepository.findById(categoryDTO.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", categoryDTO.getUserId()));

        // Check for duplicate category name for user
        if (categoryRepository.existsByNameAndUserId(categoryDTO.getName(), categoryDTO.getUserId())) {
            throw new DuplicateResourceException("Category", "name", categoryDTO.getName());
        }

        Category category = Category.builder()
                .name(categoryDTO.getName())
                .description(categoryDTO.getDescription())
                .iconName(categoryDTO.getIconName())
                .colorCode(categoryDTO.getColorCode())
                .isDefault(categoryDTO.getIsDefault() != null ? categoryDTO.getIsDefault() : false)
                .user(user)
                .build();

        Category savedCategory = categoryRepository.save(category);
        return mapToDTO(savedCategory);
    }

    /**
     * Get category by ID
     */
    @Transactional(readOnly = true)
    public CategoryDTO getCategoryById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", id));
        return mapToDTO(category);
    }

    /**
     * Get all categories for a user
     */
    @Transactional(readOnly = true)
    public List<CategoryDTO> getCategoriesByUserId(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User", "id", userId);
        }
        return categoryRepository.findByUserId(userId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Update category
     */
    public CategoryDTO updateCategory(Long id, CategoryDTO categoryDTO) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", id));

        if (categoryDTO.getName() != null)
            category.setName(categoryDTO.getName());
        if (categoryDTO.getDescription() != null)
            category.setDescription(categoryDTO.getDescription());
        if (categoryDTO.getIconName() != null)
            category.setIconName(categoryDTO.getIconName());
        if (categoryDTO.getColorCode() != null)
            category.setColorCode(categoryDTO.getColorCode());

        Category updatedCategory = categoryRepository.save(category);
        return mapToDTO(updatedCategory);
    }

    /**
     * Delete category
     */
    public void deleteCategory(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", id));
        categoryRepository.delete(category);
    }

    /**
     * Create default categories for a new user
     */
    public void createDefaultCategories(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        String[][] defaultCategories = {
                { "Food & Dining", "Restaurants, groceries, and food delivery", "#FF6B6B" },
                { "Transportation", "Gas, public transit, ride-sharing", "#4ECDC4" },
                { "Shopping", "Clothing, electronics, household items", "#9B59B6" },
                { "Bills & Utilities", "Electricity, water, internet, phone", "#3498DB" },
                { "Entertainment", "Movies, games, subscriptions", "#F39C12" },
                { "Healthcare", "Medical expenses, pharmacy, insurance", "#1ABC9C" },
                { "Education", "Courses, books, tuition", "#E74C3C" },
                { "Travel", "Flights, hotels, vacation expenses", "#2ECC71" },
                { "Other", "Miscellaneous expenses", "#95A5A6" }
        };

        for (String[] cat : defaultCategories) {
            if (!categoryRepository.existsByNameAndUserId(cat[0], userId)) {
                Category category = Category.builder()
                        .name(cat[0])
                        .description(cat[1])
                        .colorCode(cat[2])
                        .isDefault(true)
                        .user(user)
                        .build();
                categoryRepository.save(category);
            }
        }
    }

    /**
     * Map Category entity to DTO
     */
    private CategoryDTO mapToDTO(Category category) {
        BigDecimal totalExpenseAmount = expenseRepository.getTotalExpensesByUserIdAndCategoryId(
                category.getUser().getId(), category.getId());
        long expenseCount = expenseRepository.countByCategoryId(category.getId());

        return CategoryDTO.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .iconName(category.getIconName())
                .colorCode(category.getColorCode())
                .isDefault(category.getIsDefault())
                .userId(category.getUser().getId())
                .username(category.getUser().getUsername())
                .expenseCount((int) expenseCount)
                .totalExpenseAmount(totalExpenseAmount)
                .createdAt(category.getCreatedAt())
                .updatedAt(category.getUpdatedAt())
                .build();
    }
}
