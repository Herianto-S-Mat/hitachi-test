package com.example.hitachi.test.controller;

import com.example.hitachi.test.dto.ApiResponse;
import com.example.hitachi.test.dto.TransactionItemResponse;
import com.example.hitachi.test.dto.TransactionRequest;
import com.example.hitachi.test.dto.TransactionResponse;
import com.example.hitachi.test.entity.Transaction;
import com.example.hitachi.test.entity.TransactionItem;
import com.example.hitachi.test.entity.User;
import com.example.hitachi.test.service.TransactionService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/transactions")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<TransactionResponse>> createTransaction(
            @Valid @RequestBody TransactionRequest request,
            @AuthenticationPrincipal User currentUser
    ) {
        Transaction transaction = transactionService.createTransaction(request, currentUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>(true, "Transaction created successfully", convertToTransactionResponse(transaction)));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<List<TransactionResponse>>> getAllTransactions() {
        List<Transaction> transactions = transactionService.getAllTransactions();
        List<TransactionResponse> transactionResponses = transactions.stream()
                .map(this::convertToTransactionResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(new ApiResponse<>(true, "Transactions retrieved successfully", transactionResponses));
    }

    @GetMapping("/my")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<List<TransactionResponse>>> getMyTransactions(@AuthenticationPrincipal User currentUser) {
        List<Transaction> transactions = transactionService.getUserTransactions(currentUser);
        List<TransactionResponse> transactionResponses = transactions.stream()
                .map(this::convertToTransactionResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(new ApiResponse<>(true, "My transactions retrieved successfully", transactionResponses));
    }

    private TransactionResponse convertToTransactionResponse(Transaction transaction) {
        List<TransactionItemResponse> itemResponses = transaction.getTransactionItems().stream()
                .map(item -> new TransactionItemResponse(
                        item.getId(),
                        item.getProductId(),
                        item.getProductName(),
                        item.getProductPrice(),
                        item.getQuantity(),
                        item.getItemTotal(),
                        item.getTotalTaxAmount(),
                        item.getFinalPrice()
                ))
                .collect(Collectors.toList());

        return new TransactionResponse(
                transaction.getId(),
                transaction.getUser().getId(),
                transaction.getUser().getUsername(),
                transaction.getTransactionDate(),
                transaction.getTotalAmount(),
                transaction.getPaymentMethod(),
                itemResponses
        );
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
