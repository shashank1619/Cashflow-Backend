package com.cashflow.controller;

import com.cashflow.dto.ApiResponse;
import com.cashflow.dto.CategoryDTO;
import com.cashflow.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for Category operations
 * Handles expense category management
 */
@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class CategoryController {

    private final CategoryService categoryService;

    /**
     * Create a new category
     * POST /api/categories
     */
    @PostMapping
    public ResponseEntity<ApiResponse<CategoryDTO>> createCategory(@Valid @RequestBody CategoryDTO categoryDTO) {
        CategoryDTO createdCategory = categoryService.createCategory(categoryDTO);
        return new ResponseEntity<>(
                ApiResponse.success("Category created successfully", createdCategory),
                HttpStatus.CREATED);
    }

    /**
     * Get category by ID
     * GET /api/categories/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CategoryDTO>> getCategoryById(@PathVariable Long id) {
        CategoryDTO category = categoryService.getCategoryById(id);
        return ResponseEntity.ok(ApiResponse.success(category));
    }

    /**
     * Get all categories for a user (expenseCategories)
     * GET /api/categories/user/{userId}
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<CategoryDTO>>> getCategoriesByUserId(@PathVariable Long userId) {
        List<CategoryDTO> categories = categoryService.getCategoriesByUserId(userId);
        return ResponseEntity.ok(ApiResponse.success(categories));
    }

    /**
     * Update category
     * PUT /api/categories/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CategoryDTO>> updateCategory(
            @PathVariable Long id,
            @RequestBody CategoryDTO categoryDTO) {
        CategoryDTO updatedCategory = categoryService.updateCategory(id, categoryDTO);
        return ResponseEntity.ok(ApiResponse.success("Category updated successfully", updatedCategory));
    }

    /**
     * Delete category
     * DELETE /api/categories/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.ok(ApiResponse.success("Category deleted successfully", null));
    }

    /**
     * Create default categories for user
     * POST /api/categories/user/{userId}/defaults
     */
    @PostMapping("/user/{userId}/defaults")
    public ResponseEntity<ApiResponse<Void>> createDefaultCategories(@PathVariable Long userId) {
        categoryService.createDefaultCategories(userId);
        return ResponseEntity.ok(ApiResponse.success("Default categories created successfully", null));
    }
}
