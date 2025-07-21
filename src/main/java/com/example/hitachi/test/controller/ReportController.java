package com.example.hitachi.test.controller;

import com.example.hitachi.test.dto.ApiResponse;
import com.example.hitachi.test.dto.CustomerSpendingReportResponse;
import com.example.hitachi.test.dto.ProductSpendingReportResponse;
import com.example.hitachi.test.dto.TaxSpendingReportResponse;
import com.example.hitachi.test.service.ReportService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/reports")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasRole('SUPER_ADMIN')") // All reports require SUPER_ADMIN
public class ReportController {

    private final ReportService reportService;

    @GetMapping("/customer-spending")
    public ResponseEntity<ApiResponse<List<CustomerSpendingReportResponse>>> getCustomerSpendingReport(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate
    ) {
        List<CustomerSpendingReportResponse> reports = reportService.getCustomerSpendingReport(startDate, endDate);
        return ResponseEntity.ok(new ApiResponse<>(true, "Customer spending report generated successfully", reports));
    }

    @GetMapping("/tax-spending")
    public ResponseEntity<ApiResponse<List<TaxSpendingReportResponse>>> getTaxSpendingReport() {
        List<TaxSpendingReportResponse> reports = reportService.getTaxSpendingReport();
        return ResponseEntity.ok(new ApiResponse<>(true, "Tax spending report generated successfully", reports));
    }

    @GetMapping("/product-spending")
    public ResponseEntity<ApiResponse<List<ProductSpendingReportResponse>>> getProductSpendingReport() {
        List<ProductSpendingReportResponse> reports = reportService.getProductSpendingReport();
        return ResponseEntity.ok(new ApiResponse<>(true, "Product spending report generated successfully", reports));
    }
}
