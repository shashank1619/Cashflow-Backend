package com.cashflow.controller;

import com.cashflow.dto.ApiResponse;
import com.cashflow.dto.MonthlyStatsDTO;
import com.cashflow.dto.MonthlyTrendDTO;
import com.cashflow.service.StatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.YearMonth;
import java.util.List;

/**
 * REST Controller for Statistics and Analytics
 */
@RestController
@RequestMapping("/api/stats")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class StatsController {

    private final StatsService statsService;

    /**
     * Get monthly statistics for a specific month
     * GET /api/stats/monthly/{userId}?year=2026&month=1
     */
    @GetMapping("/monthly/{userId}")
    public ResponseEntity<ApiResponse<MonthlyStatsDTO>> getMonthlyStats(
            @PathVariable Long userId,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month) {

        // Default to current month if not specified
        YearMonth current = YearMonth.now();
        int targetYear = year != null ? year : current.getYear();
        int targetMonth = month != null ? month : current.getMonthValue();

        MonthlyStatsDTO stats = statsService.getMonthlyStats(userId, targetYear, targetMonth);
        return ResponseEntity.ok(ApiResponse.success(stats));
    }

    /**
     * Get monthly trends for last N months (default 6)
     * GET /api/stats/trends/{userId}?months=6&categoryId=1
     */
    @GetMapping("/trends/{userId}")
    public ResponseEntity<ApiResponse<List<MonthlyTrendDTO>>> getMonthlyTrends(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "6") int months,
            @RequestParam(required = false) Long categoryId) {

        List<MonthlyTrendDTO> trends;
        if (categoryId != null) {
            trends = statsService.getMonthlyTrendsByCategory(userId, months, categoryId);
        } else {
            trends = statsService.getMonthlyTrends(userId, months);
        }
        return ResponseEntity.ok(ApiResponse.success(trends));
    }
}
