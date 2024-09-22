package com.example.compass.controller;

import com.example.compass.dto.BatchPaymentDTO;
import com.example.compass.dto.PaymentDTO;
import com.example.compass.service.BillingCodeService;
import com.example.compass.service.SellerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class BatchPaymentController {

    private final SellerService sellerService;
    private final BillingCodeService billingCodeService;

    @Autowired
    public BatchPaymentController(SellerService sellerService, BillingCodeService billingCodeService) {
        this.sellerService = sellerService;
        this.billingCodeService = billingCodeService;
    }

    @PostMapping("/batch-payment")
    public ResponseEntity<?> processBatchPayment(@RequestBody BatchPaymentDTO batchPayment) {
        // Extract the seller code and payment information from the BatchPaymentDTO
        Long sellerId = batchPayment.getSellerId();
        List<PaymentDTO> payments = batchPayment.getPayments();

        // Use the SellerService to process the seller code
        sellerService.getSellerById(sellerId);

        // Use the BillingCodeService to process the payment information
        for (PaymentDTO payment : payments) {
            billingCodeService.validatePaymentAmount(payment.getOriginalAmount(), payment.getAmount().doubleValue());
        }

        // Return a response
        return ResponseEntity.ok().build();
    }
}