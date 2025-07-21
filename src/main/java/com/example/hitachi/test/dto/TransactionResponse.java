package com.example.hitachi.test.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionResponse {
    private Long id;
    private Long userId;
    private String username;
    private LocalDateTime transactionDate;
    private BigDecimal totalAmount;
    private String paymentMethod;
    private List<TransactionItemResponse> items;
}
