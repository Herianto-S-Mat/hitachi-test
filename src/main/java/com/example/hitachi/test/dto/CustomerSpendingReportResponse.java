package com.example.hitachi.test.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerSpendingReportResponse {
    private Long userId;
    private String username;
    private String email;
    private BigDecimal totalSpentOverall;
    private BigDecimal totalSpentBetweenDates; // Null if no dates provided
}
