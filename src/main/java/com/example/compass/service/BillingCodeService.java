package com.example.compass.service;

import com.example.compass.entity.BillingCode;
import com.example.compass.repository.BillingCodeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BillingCodeService {

    private final BillingCodeRepository billingCodeRepository;

    @Autowired
    public BillingCodeService(BillingCodeRepository billingCodeRepository) {
        this.billingCodeRepository = billingCodeRepository;
    }

    public List<BillingCode> getAllBillingCodes() {
        return billingCodeRepository.findAll();
    }

    public Optional<BillingCode> getBillingCodeById(Long id) {
        return billingCodeRepository.findById(id);
    }

    public BillingCode saveBillingCode(BillingCode billingCode) {
        return billingCodeRepository.save(billingCode);
    }

    public void deleteBillingCode(Long id) {
        billingCodeRepository.deleteById(id);
    }

    public String validatePaymentAmount(double originalAmount, double paymentAmount) {
        if (paymentAmount < originalAmount) {
            return "Partial payment";
        } else if (paymentAmount == originalAmount) {
            return "Full payment";
        } else {
            return "Overpayment";
        }
    }
}