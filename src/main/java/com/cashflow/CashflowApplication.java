package com.cashflow;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Cashflow Application - Main Entry Point
 * 
 * A comprehensive cash flow monitoring system that enables users to:
 * - Track expenses and categorize them
 * - Monitor credits and income
 * - Set expense thresholds and receive alerts
 * - View overall financial summary
 * 
 * @author Shashank Kumar
 * @version 1.0.0
 */
@SpringBootApplication
public class CashflowApplication {

    public static void main(String[] args) {
        SpringApplication.run(CashflowApplication.class, args);
        System.out.println("\n========================================");
        System.out.println("  CASHFLOW BACKEND STARTED SUCCESSFULLY");
        System.out.println("  API Base URL: http://localhost:8080/api");
        System.out.println("  H2 Console: http://localhost:8080/h2-console");
        System.out.println("========================================\n");
    }
}
