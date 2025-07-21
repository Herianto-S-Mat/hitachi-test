package com.example.hitachi.test.service;

import com.example.hitachi.test.dto.CustomerSpendingReportResponse;
import com.example.hitachi.test.dto.ProductSpendingReportResponse;
import com.example.hitachi.test.dto.TaxSpendingReportResponse;
import com.example.hitachi.test.entity.Transaction;
import com.example.hitachi.test.entity.TransactionItem;
import com.example.hitachi.test.entity.User;
import com.example.hitachi.test.repository.TransactionRepository;
import com.example.hitachi.test.repository.TransactionItemRepository;
import com.example.hitachi.test.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final TransactionRepository transactionRepository;
    private final TransactionItemRepository transactionItemRepository;
    private final UserRepository userRepository;

    public List<CustomerSpendingReportResponse> getCustomerSpendingReport(LocalDateTime startDate, LocalDateTime endDate) {
        List<User> allUsers = userRepository.findAll();

        return allUsers.stream().map(user -> {
            BigDecimal totalSpentOverall = transactionRepository.findByUser(user).stream()
                    .map(Transaction::getTotalAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal totalSpentBetweenDates = BigDecimal.ZERO;
            if (startDate != null && endDate != null) {
                totalSpentBetweenDates = transactionRepository.findByUser(user).stream()
                        .filter(t -> t.getTransactionDate().isAfter(startDate) && t.getTransactionDate().isBefore(endDate))
                        .map(Transaction::getTotalAmount)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
            }

            return new CustomerSpendingReportResponse(
                    user.getId(),
                    user.getUsername(),
                    user.getEmail(),
                    totalSpentOverall,
                    (startDate != null && endDate != null) ? totalSpentBetweenDates : null
            );
        }).collect(Collectors.toList());
    }

    public List<TaxSpendingReportResponse> getTaxSpendingReport() {
        List<TransactionItem> allTransactionItems = transactionItemRepository.findAll();

        Map<String, BigDecimal> taxAmounts = allTransactionItems.stream()
                .collect(Collectors.groupingBy(
                        TransactionItem::getProductName, // Group by product name for simplicity, ideally by tax name if stored in TransactionItem
                        Collectors.reducing(BigDecimal.ZERO, TransactionItem::getTotalTaxAmount, BigDecimal::add)
                ));

        // This part needs refinement if tax names are not directly in TransactionItem
        // For now, it groups by product name and sums up tax amounts for that product.
        // If you need per-tax-name reporting, you'd need to store tax details in TransactionItem or join with Product/Tax entities.
        return taxAmounts.entrySet().stream()
                .map(entry -> new TaxSpendingReportResponse(null, entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
    }

    public List<ProductSpendingReportResponse> getProductSpendingReport() {
        List<TransactionItem> allTransactionItems = transactionItemRepository.findAll();

        Map<Long, List<TransactionItem>> itemsByProductId = allTransactionItems.stream()
                .collect(Collectors.groupingBy(TransactionItem::getProductId));

        return itemsByProductId.entrySet().stream().map(entry -> {
            Long productId = entry.getKey();
            List<TransactionItem> items = entry.getValue();

            BigDecimal totalRevenue = items.stream()
                    .map(TransactionItem::getFinalPrice)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            Long totalQuantitySold = items.stream()
                    .mapToLong(TransactionItem::getQuantity)
                    .sum();

            // Assuming product name is consistent for a given product ID
            String productName = items.isEmpty() ? "Unknown Product" : items.get(0).getProductName();

            return new ProductSpendingReportResponse(productId, productName, totalRevenue, totalQuantitySold);
        }).collect(Collectors.toList());
    }
}
