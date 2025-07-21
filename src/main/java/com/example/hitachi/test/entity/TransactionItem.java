package com.example.hitachi.test.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "transaction_items")
public class TransactionItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transaction_id", nullable = false)
    private Transaction transaction;

    @Column(name = "product_id", nullable = false)
    private Long productId; // Store original product ID

    @Column(name = "product_name", nullable = false)
    private String productName; // Snapshot of product name

    @Column(name = "product_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal productPrice; // Snapshot of product price (tax-excluded)

    @Column(nullable = false)
    private Integer quantity;

    @Column(name = "item_total", nullable = false, precision = 12, scale = 2)
    private BigDecimal itemTotal; // (productPrice * quantity)

    @Column(name = "total_tax_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal totalTaxAmount; // Sum of all taxes for this item

    @Column(name = "final_price", nullable = false, precision = 12, scale = 2)
    private BigDecimal finalPrice; // itemTotal + totalTaxAmount
}