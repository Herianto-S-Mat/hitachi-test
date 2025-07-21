package com.example.hitachi.test.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.DecimalMin;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductRequest {
    @NotBlank(message = "Product name cannot be empty")
    @Schema(example = "Laptop")
    private String name;

    @NotNull(message = "Product price cannot be null")
    @DecimalMin(value = "0.0", inclusive = false, message = "Product price must be positive")
    @Schema(example = "1200.00")
    private BigDecimal price;

    @Schema(description = "Set of Tax IDs to apply to the product", example = "[1, 2]")
    private Set<Long> taxIds;
}
