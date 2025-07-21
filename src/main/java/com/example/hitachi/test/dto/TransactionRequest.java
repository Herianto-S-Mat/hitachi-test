package com.example.hitachi.test.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionRequest {
    @NotBlank(message = "Payment method cannot be empty")
    @Schema(example = "Credit Card")
    private String paymentMethod;

    @NotEmpty(message = "Transaction items cannot be empty")
    @Valid
    private List<TransactionItemRequest> items;
}
