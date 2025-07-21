package com.example.hitachi.test.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaxSpendingReportResponse {
    private Long taxId;
    private String taxName;
    private BigDecimal totalTaxCollected;
}
