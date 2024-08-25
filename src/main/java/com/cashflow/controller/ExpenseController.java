package com.cashflow.controller;

import com.cashflow.dto.ApiResponse;
import com.cashflow.dto.ExpenseDTO;
import com.cashflow.dto.ExpenseSummaryDTO;
import com.cashflow.service.ExpenseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * REST Controller for Expense operations
 * Handles expense tracking and summaries
 */
@RestController
@RequestMapping("/api/expenses")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ExpenseController {

    private final ExpenseService expenseService;

    /**
     * Add a new expense
     * POST /api/expenses
     */
    @PostMapping
    public ResponseEntity<ApiResponse<ExpenseDTO>> addExpense(@Valid @RequestBody ExpenseDTO expenseDTO) {
        ExpenseDTO createdExpense = expenseService.addExpense(expenseDTO);
        return new ResponseEntity<>(
                ApiResponse.success("Expense added successfully", createdExpense),
                HttpStatus.CREATED);
    }

    /**
     * Get expense by ID
     * GET /api/expenses/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ExpenseDTO>> getExpenseById(@PathVariable Long id) {
        ExpenseDTO expense = expenseService.getExpenseById(id);
        return ResponseEntity.ok(ApiResponse.success(expense));
    }

    /**
     * Get all expenses for a user
     * GET /api/expenses/user/{userId}
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<ExpenseDTO>>> getExpensesByUserId(@PathVariable Long userId) {
        List<ExpenseDTO> expenses = expenseService.getExpensesByUserId(userId);
        return ResponseEntity.ok(ApiResponse.success(expenses));
    }

    /**
     * Get user's overall expense summary (userOverAllExpense)
     * GET /api/expenses/user/{userId}/summary
     */
    @GetMapping("/user/{userId}/summary")
    public ResponseEntity<ApiResponse<ExpenseSummaryDTO>> getUserOverallExpense(@PathVariable Long userId) {
        ExpenseSummaryDTO summary = expenseService.getUserOverallExpense(userId);
        return ResponseEntity.ok(ApiResponse.success(summary));
    }

    /**
     * Get expenses by category for a user (categoryExpense)
     * GET /api/expenses/user/{userId}/category/{categoryId}
     */
    @GetMapping("/user/{userId}/category/{categoryId}")
    public ResponseEntity<ApiResponse<List<ExpenseDTO>>> getExpensesByCategory(
            @PathVariable Long userId,
            @PathVariable Long categoryId) {
        List<ExpenseDTO> expenses = expenseService.getExpensesByCategory(userId, categoryId);
        return ResponseEntity.ok(ApiResponse.success(expenses));
    }

    /**
     * Get expense summary by date range
     * GET /api/expenses/user/{userId}/summary/range
     */
    @GetMapping("/user/{userId}/summary/range")
    public ResponseEntity<ApiResponse<ExpenseSummaryDTO>> getExpenseSummaryByDateRange(
            @PathVariable Long userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        ExpenseSummaryDTO summary = expenseService.getExpenseSummaryByDateRange(userId, startDate, endDate);
        return ResponseEntity.ok(ApiResponse.success(summary));
    }

    /**
     * Update expense
     * PUT /api/expenses/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ExpenseDTO>> updateExpense(
            @PathVariable Long id,
            @RequestBody ExpenseDTO expenseDTO) {
        ExpenseDTO updatedExpense = expenseService.updateExpense(id, expenseDTO);
        return ResponseEntity.ok(ApiResponse.success("Expense updated successfully", updatedExpense));
    }

    /**
     * Delete expense
     * DELETE /api/expenses/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteExpense(@PathVariable Long id) {
        expenseService.deleteExpense(id);
        return ResponseEntity.ok(ApiResponse.success("Expense deleted successfully", null));
    }
}
// Expense date filters
