package com.example.hitachi.test.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductSpendingReportResponse {
    private Long productId;
    private String productName;
    private BigDecimal totalRevenue;
    private Long totalQuantitySold;
}
