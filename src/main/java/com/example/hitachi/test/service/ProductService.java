package com.example.hitachi.test.service;

import com.example.hitachi.test.dto.ProductRequest;
import com.example.hitachi.test.entity.Product;
import com.example.hitachi.test.entity.Tax;
import com.example.hitachi.test.repository.ProductRepository;
import com.example.hitachi.test.repository.TaxRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final TaxRepository taxRepository;

    public Product createProduct(ProductRequest request) {
        Product product = Product.builder()
                .name(request.getName())
                .price(request.getPrice())
                .build();

        if (request.getTaxIds() != null && !request.getTaxIds().isEmpty()) {
            Set<Tax> taxes = new HashSet<>(taxRepository.findAllById(request.getTaxIds()));
            if (taxes.size() != request.getTaxIds().size()) {
                throw new RuntimeException("One or more tax IDs not found");
            }
            product.setTaxes(taxes);
        }

        return productRepository.save(product);
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id " + id));
    }

    public Product updateProduct(Long id, ProductRequest request) {
        Product existingProduct = getProductById(id);
        existingProduct.setName(request.getName());
        existingProduct.setPrice(request.getPrice());

        if (request.getTaxIds() != null) {
            Set<Tax> taxes = new HashSet<>(taxRepository.findAllById(request.getTaxIds()));
            if (taxes.size() != request.getTaxIds().size()) {
                throw new RuntimeException("One or more tax IDs not found");
            }
            existingProduct.setTaxes(taxes);
        } else {
            existingProduct.setTaxes(new HashSet<>()); // Clear taxes if taxIds is null
        }

        return productRepository.save(existingProduct);
    }

    public void deleteProduct(Long id) {
        Product product = getProductById(id);
        productRepository.delete(product);
    }
}
