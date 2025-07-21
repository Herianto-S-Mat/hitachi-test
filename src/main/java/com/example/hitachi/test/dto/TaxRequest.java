package com.example.hitachi.test.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.DecimalMin;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaxRequest {
    @NotBlank(message = "Tax name cannot be empty")
    @Schema(example = "VAT")
    private String name;

    @NotNull(message = "Tax rate cannot be null")
    @DecimalMin(value = "0.0", inclusive = true, message = "Tax rate must be non-negative")
    @Schema(example = "0.10")
    private BigDecimal rate;
}
