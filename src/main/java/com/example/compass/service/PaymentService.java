package com.example.compass.service;

import com.example.compass.dto.BatchPaymentDTO;
import com.example.compass.dto.PaymentDTO;
import com.example.compass.entity.BillingCode;
import com.example.compass.entity.Payment;
import com.example.compass.entity.Seller;
import com.example.compass.repository.BillingCodeRepository;
import com.example.compass.repository.PaymentRepository;
import com.example.compass.repository.SellerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final SellerRepository sellerRepository;

    private final BillingCodeRepository billingCodeRepository;

    @Autowired
    public PaymentService(PaymentRepository paymentRepository, SellerRepository sellerRepository, BillingCodeRepository billingCodeRepository) {
        this.paymentRepository = paymentRepository;
        this.sellerRepository = sellerRepository;
        this.billingCodeRepository = billingCodeRepository;
    }

    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }

    public Payment getPaymentById(Long id) {
        return paymentRepository.findById(id).orElse(null);
    }

    public Payment savePayment(Payment payment) {
        return paymentRepository.save(payment);
    }

    public void deletePayment(Long id) {
        paymentRepository.deleteById(id);
    }

    //****

    // In PaymentService.java

    public void processBatchPayments(BatchPaymentDTO batchPaymentDTO) throws Exception {
        Seller seller = sellerRepository.findById(batchPaymentDTO.getSellerId())
                .orElseThrow(() -> new Exception("Seller not found"));

        for (PaymentDTO paymentDTO : batchPaymentDTO.getPayments()) {
            BillingCode billingCode = billingCodeRepository.findById(paymentDTO.getBillingCodeId())
                    .orElseThrow(() -> new Exception("Billing code not found"));

            validatePaymentAmount(paymentDTO.getAmount(), billingCode.getAmount());

            Payment payment = new Payment();
            // set payment properties here

            paymentRepository.save(payment);
        }
    }

    private void validatePaymentAmount(BigDecimal paymentAmount, BigDecimal originalAmount) throws Exception {
        if (paymentAmount.compareTo(originalAmount) < 0) {
            // Payment is partial
        } else if (paymentAmount.compareTo(originalAmount) == 0) {
            // Payment is total
        } else if (paymentAmount.compareTo(originalAmount) > 0) {
            // Payment is excess
        } else {
            throw new Exception("Invalid payment amount");
        }
    }

}