package com.example.hitachi.test.service;

import com.example.hitachi.test.dto.TransactionItemRequest;
import com.example.hitachi.test.dto.TransactionRequest;
import com.example.hitachi.test.entity.Product;
import com.example.hitachi.test.entity.Tax;
import com.example.hitachi.test.entity.Transaction;
import com.example.hitachi.test.entity.TransactionItem;
import com.example.hitachi.test.entity.User;
import com.example.hitachi.test.repository.ProductRepository;
import com.example.hitachi.test.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final ProductRepository productRepository;

    @Transactional
    public Transaction createTransaction(TransactionRequest request, User currentUser) {
        Transaction transaction = new Transaction();
        transaction.setUser(currentUser);
        transaction.setPaymentMethod(request.getPaymentMethod());

        BigDecimal totalAmount = BigDecimal.ZERO;
        List<TransactionItem> transactionItems = new ArrayList<>();

        for (TransactionItemRequest itemRequest : request.getItems()) {
            Product product = productRepository.findById(itemRequest.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found with ID: " + itemRequest.getProductId()));

            BigDecimal itemBasePrice = product.getPrice().multiply(BigDecimal.valueOf(itemRequest.getQuantity()));
            BigDecimal itemTaxAmount = calculateTotalTaxForProduct(product, itemRequest.getQuantity());
            BigDecimal itemFinalPrice = itemBasePrice.add(itemTaxAmount);

            TransactionItem transactionItem = TransactionItem.builder()
                    .productId(product.getId())
                    .productName(product.getName())
                    .productPrice(product.getPrice())
                    .quantity(itemRequest.getQuantity())
                    .itemTotal(itemBasePrice) // Base price * quantity
                    .totalTaxAmount(itemTaxAmount)
                    .finalPrice(itemFinalPrice)
                    .transaction(transaction)
                    .build();
            transactionItems.add(transactionItem);
            totalAmount = totalAmount.add(itemFinalPrice);
        }

        transaction.setTransactionItems(transactionItems);
        transaction.setTotalAmount(totalAmount.setScale(2, RoundingMode.HALF_UP));

        return transactionRepository.save(transaction);
    }

    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }

    public List<Transaction> getUserTransactions(User user) {
        return transactionRepository.findByUser(user);
    }

    private BigDecimal calculateTotalTaxForProduct(Product product, Integer quantity) {
        BigDecimal totalTaxRate = BigDecimal.ZERO;
        Set<Tax> taxes = product.getTaxes();
        if (taxes != null) {
            for (Tax tax : taxes) {
                totalTaxRate = totalTaxRate.add(tax.getRate());
            }
        }
        // Calculate tax amount: (product price * quantity) * total tax rate
        return product.getPrice()
                .multiply(BigDecimal.valueOf(quantity))
                .multiply(totalTaxRate)
                .setScale(2, RoundingMode.HALF_UP);
    }
}
