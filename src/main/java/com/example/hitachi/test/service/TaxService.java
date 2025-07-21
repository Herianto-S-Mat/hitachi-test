package com.example.hitachi.test.service;

import com.example.hitachi.test.dto.TaxRequest;
import com.example.hitachi.test.entity.Tax;
import com.example.hitachi.test.repository.TaxRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TaxService {

    private final TaxRepository taxRepository;

    public Tax createTax(TaxRequest request) {
        if (taxRepository.findByName(request.getName()).isPresent()) {
            throw new RuntimeException("Tax with name " + request.getName() + " already exists");
        }
        Tax tax = Tax.builder()
                .name(request.getName())
                .rate(request.getRate())
                .build();
        return taxRepository.save(tax);
    }

    public List<Tax> getAllTaxes() {
        return taxRepository.findAll();
    }

    public Tax getTaxById(Long id) {
        return taxRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tax not found with id " + id));
    }

    public Tax updateTax(Long id, TaxRequest request) {
        Tax existingTax = getTaxById(id);
        if (!existingTax.getName().equals(request.getName()) && taxRepository.findByName(request.getName()).isPresent()) {
            throw new RuntimeException("Tax with name " + request.getName() + " already exists");
        }
        existingTax.setName(request.getName());
        existingTax.setRate(request.getRate());
        return taxRepository.save(existingTax);
    }

    public void deleteTax(Long id) {
        Tax tax = getTaxById(id);
        taxRepository.delete(tax);
    }
}
