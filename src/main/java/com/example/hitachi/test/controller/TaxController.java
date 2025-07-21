package com.example.hitachi.test.controller;

import com.example.hitachi.test.dto.ApiResponse;
import com.example.hitachi.test.dto.TaxRequest;
import com.example.hitachi.test.dto.TaxResponse;
import com.example.hitachi.test.entity.Tax;
import com.example.hitachi.test.service.TaxService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/taxes")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class TaxController {

    private final TaxService taxService;

    @PostMapping
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<TaxResponse>> createTax(@Valid @RequestBody TaxRequest request) {
        Tax tax = taxService.createTax(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>(true, "Tax created successfully", convertToTaxResponse(tax)));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<List<TaxResponse>>> getAllTaxes() {
        List<Tax> taxes = taxService.getAllTaxes();
        List<TaxResponse> taxResponses = taxes.stream()
                .map(this::convertToTaxResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(new ApiResponse<>(true, "Taxes retrieved successfully", taxResponses));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<TaxResponse>> getTaxById(@PathVariable Long id) {
        Tax tax = taxService.getTaxById(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Tax retrieved successfully", convertToTaxResponse(tax)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<TaxResponse>> updateTax(@PathVariable Long id, @Valid @RequestBody TaxRequest request) {
        Tax updatedTax = taxService.updateTax(id, request);
        return ResponseEntity.ok(new ApiResponse<>(true, "Tax updated successfully", convertToTaxResponse(updatedTax)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<String>> deleteTax(@PathVariable Long id) {
        taxService.deleteTax(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Tax deleted successfully", null));
    }

    private TaxResponse convertToTaxResponse(Tax tax) {
        return new TaxResponse(tax.getId(), tax.getName(), tax.getRate());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((org.springframework.validation.FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return errors;
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiResponse<String>> handleRuntimeException(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiResponse<>(false, ex.getMessage(), null));
    }
}
