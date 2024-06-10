package com.cashflow.controller;

import com.cashflow.dto.ApiResponse;
import com.cashflow.dto.CreditDTO;
import com.cashflow.service.CreditService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * REST Controller for Credit operations
 * Handles user income/credits tracking (userCredit)
 */
@RestController
@RequestMapping("/api/credits")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class CreditController {

    private final CreditService creditService;

    /**
     * Add a new credit
     * POST /api/credits
     */
    @PostMapping
    public ResponseEntity<ApiResponse<CreditDTO>> addCredit(@Valid @RequestBody CreditDTO creditDTO) {
        CreditDTO createdCredit = creditService.addCredit(creditDTO);
        return new ResponseEntity<>(
                ApiResponse.success("Credit added successfully", createdCredit),
                HttpStatus.CREATED);
    }

    /**
     * Get credit by ID
     * GET /api/credits/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CreditDTO>> getCreditById(@PathVariable Long id) {
        CreditDTO credit = creditService.getCreditById(id);
        return ResponseEntity.ok(ApiResponse.success(credit));
    }

    /**
     * Get all credits for a user (userCredit)
     * GET /api/credits/user/{userId}
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<CreditDTO>>> getCreditsByUserId(@PathVariable Long userId) {
        List<CreditDTO> credits = creditService.getCreditsByUserId(userId);
        return ResponseEntity.ok(ApiResponse.success(credits));
    }

    /**
     * Get total credits for a user
     * GET /api/credits/user/{userId}/total
     */
    @GetMapping("/user/{userId}/total")
    public ResponseEntity<ApiResponse<BigDecimal>> getTotalCredits(@PathVariable Long userId) {
        BigDecimal totalCredits = creditService.getTotalCredits(userId);
        return ResponseEntity.ok(ApiResponse.success("Total credits retrieved", totalCredits));
    }

    /**
     * Get credits by date range
     * GET /api/credits/user/{userId}/range
     */
    @GetMapping("/user/{userId}/range")
    public ResponseEntity<ApiResponse<List<CreditDTO>>> getCreditsByDateRange(
            @PathVariable Long userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<CreditDTO> credits = creditService.getCreditsByDateRange(userId, startDate, endDate);
        return ResponseEntity.ok(ApiResponse.success(credits));
    }

    /**
     * Get credits by source
     * GET /api/credits/user/{userId}/source/{source}
     */
    @GetMapping("/user/{userId}/source/{source}")
    public ResponseEntity<ApiResponse<List<CreditDTO>>> getCreditsBySource(
            @PathVariable Long userId,
            @PathVariable String source) {
        List<CreditDTO> credits = creditService.getCreditsBySource(userId, source);
        return ResponseEntity.ok(ApiResponse.success(credits));
    }

    /**
     * Update credit
     * PUT /api/credits/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CreditDTO>> updateCredit(
            @PathVariable Long id,
            @RequestBody CreditDTO creditDTO) {
        CreditDTO updatedCredit = creditService.updateCredit(id, creditDTO);
        return ResponseEntity.ok(ApiResponse.success("Credit updated successfully", updatedCredit));
    }

    /**
     * Delete credit
     * DELETE /api/credits/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteCredit(@PathVariable Long id) {
        creditService.deleteCredit(id);
        return ResponseEntity.ok(ApiResponse.success("Credit deleted successfully", null));
    }
}
