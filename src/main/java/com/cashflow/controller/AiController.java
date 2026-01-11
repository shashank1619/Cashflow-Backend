package com.cashflow.controller;

import com.cashflow.dto.ApiResponse;
import com.cashflow.service.AiService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * REST Controller for AI Chat functionality
 */
@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AiController {

    private final AiService aiService;

    /**
     * Chat with AI about expenses
     * POST /api/ai/chat
     * Body: { "userId": 1, "message": "How much did I spend on food?" }
     */
    @PostMapping("/chat")
    public ResponseEntity<ApiResponse<Map<String, String>>> chat(
            @RequestBody Map<String, Object> request) {

        Long userId = Long.valueOf(request.get("userId").toString());
        String message = request.get("message").toString();

        String response = aiService.chat(userId, message);

        return ResponseEntity.ok(ApiResponse.success(Map.of(
                "response", response)));
    }
}
