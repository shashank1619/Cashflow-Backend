package com.cashflow.controller;

import com.cashflow.dto.AlertDTO;
import com.cashflow.dto.ApiResponse;
import com.cashflow.dto.ThresholdDTO;
import com.cashflow.service.AlertService;
import com.cashflow.service.ThresholdService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for Threshold operations
 * Handles expense threshold management and alerts (setThreshold,
 * thresholdBreachedAlert)
 */
@RestController
@RequestMapping("/api/thresholds")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ThresholdController {

    private final ThresholdService thresholdService;
    private final AlertService alertService;

    /**
     * Set a new threshold (setThreshold)
     * POST /api/thresholds
     */
    @PostMapping
    public ResponseEntity<ApiResponse<ThresholdDTO>> setThreshold(@Valid @RequestBody ThresholdDTO thresholdDTO) {
        ThresholdDTO createdThreshold = thresholdService.setThreshold(thresholdDTO);
        return new ResponseEntity<>(
                ApiResponse.success("Threshold set successfully", createdThreshold),
                HttpStatus.CREATED);
    }

    /**
     * Get threshold by ID
     * GET /api/thresholds/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ThresholdDTO>> getThresholdById(@PathVariable Long id) {
        ThresholdDTO threshold = thresholdService.getThresholdById(id);
        return ResponseEntity.ok(ApiResponse.success(threshold));
    }

    /**
     * Get all thresholds for a user
     * GET /api/thresholds/user/{userId}
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<ThresholdDTO>>> getThresholdsByUserId(@PathVariable Long userId) {
        List<ThresholdDTO> thresholds = thresholdService.getThresholdsByUserId(userId);
        return ResponseEntity.ok(ApiResponse.success(thresholds));
    }

    /**
     * Get active thresholds for a user
     * GET /api/thresholds/user/{userId}/active
     */
    @GetMapping("/user/{userId}/active")
    public ResponseEntity<ApiResponse<List<ThresholdDTO>>> getActiveThresholds(@PathVariable Long userId) {
        List<ThresholdDTO> thresholds = thresholdService.getActiveThresholds(userId);
        return ResponseEntity.ok(ApiResponse.success(thresholds));
    }

    /**
     * Get overall threshold for a user
     * GET /api/thresholds/user/{userId}/overall
     */
    @GetMapping("/user/{userId}/overall")
    public ResponseEntity<ApiResponse<ThresholdDTO>> getOverallThreshold(@PathVariable Long userId) {
        ThresholdDTO threshold = thresholdService.getOverallThreshold(userId);
        return ResponseEntity.ok(ApiResponse.success(threshold));
    }

    /**
     * Get threshold breach alerts for a user (thresholdBreachedAlert)
     * GET /api/thresholds/alerts/{userId}
     */
    @GetMapping("/alerts/{userId}")
    public ResponseEntity<ApiResponse<List<AlertDTO>>> getThresholdBreachedAlerts(@PathVariable Long userId) {
        List<AlertDTO> alerts = alertService.getThresholdBreachedAlerts(userId);
        return ResponseEntity.ok(ApiResponse.success(alerts));
    }

    /**
     * Check and get current threshold status/alerts for a user
     * GET /api/thresholds/check/{userId}
     */
    @GetMapping("/check/{userId}")
    public ResponseEntity<ApiResponse<List<AlertDTO>>> checkThresholds(@PathVariable Long userId) {
        List<AlertDTO> alerts = alertService.checkThresholdBreaches(userId);
        return ResponseEntity.ok(ApiResponse.success(alerts));
    }

    /**
     * Update threshold
     * PUT /api/thresholds/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ThresholdDTO>> updateThreshold(
            @PathVariable Long id,
            @RequestBody ThresholdDTO thresholdDTO) {
        ThresholdDTO updatedThreshold = thresholdService.updateThreshold(id, thresholdDTO);
        return ResponseEntity.ok(ApiResponse.success("Threshold updated successfully", updatedThreshold));
    }

    /**
     * Delete threshold
     * DELETE /api/thresholds/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteThreshold(@PathVariable Long id) {
        thresholdService.deleteThreshold(id);
        return ResponseEntity.ok(ApiResponse.success("Threshold deleted successfully", null));
    }

    /**
     * Toggle threshold active status
     * PATCH /api/thresholds/{id}/toggle
     */
    @PatchMapping("/{id}/toggle")
    public ResponseEntity<ApiResponse<ThresholdDTO>> toggleThresholdStatus(@PathVariable Long id) {
        ThresholdDTO threshold = thresholdService.toggleThresholdStatus(id);
        return ResponseEntity.ok(ApiResponse.success("Threshold status toggled", threshold));
    }
}
// Threshold alerts endpoint
