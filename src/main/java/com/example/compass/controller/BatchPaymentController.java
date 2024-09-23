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

import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
public class BatchPaymentController {

    private final SellerService sellerService;
    private final BillingCodeService billingCodeService;
    private final SqsClient sqsClient;

    @Autowired
    public BatchPaymentController(SellerService sellerService, BillingCodeService billingCodeService, SqsClient sqsClient) {
        this.sellerService = sellerService;
        this.billingCodeService = billingCodeService;
        this.sqsClient = sqsClient;
    }

    @PostMapping("/batch-payment")
    public ResponseEntity<?> processBatchPayment(@RequestBody BatchPaymentDTO batchPayment) {
        // Extract the seller code and payment information from the BatchPaymentDTO
        Long sellerId = batchPayment.getSellerId();
        List<PaymentDTO> payments = batchPayment.getPayments();

        // Validate seller existence
        if (sellerService.getSellerById(sellerId) == null) {
            return ResponseEntity.status(404).body(Map.of("error", "Seller not found"));
        }

        // Use the BillingCodeService to process the payment information
        for (PaymentDTO payment : payments) {

            if (payment.getAmount() == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Missing payment amount"));
            }

            billingCodeService.validatePaymentAmount(payment.getOriginalAmount(), payment.getAmount().doubleValue());

            BigDecimal originalAmount = BigDecimal.valueOf(payment.getOriginalAmount());
            BigDecimal paidAmount = payment.getAmount();
            String queueUrl;

            if (paidAmount.compareTo(originalAmount) < 0) {
                // The payment is partial

                // Send to the partial payment SQS queue
                queueUrl = "partialPaymentQueue";
                payment.setStatus("PARTIAL");

            } else if (paidAmount.compareTo(originalAmount) == 0) {
                // The payment is total

                // Send to the total payment SQS queue
                queueUrl = "totalPaymentQueue";
                payment.setStatus("FULL");

            } else {
                // The payment is excess

                // Send to the excess payment SQS queue
                queueUrl = "excessPaymentQueue";
                payment.setStatus("OVERPAID");

            }

            String fullQueueUrl = sqsClient.getQueueUrl(builder -> builder.queueName(queueUrl)).queueUrl();

            // Send message to the appropriate SQS queue
            sqsClient.sendMessage(SendMessageRequest.builder()
                    .queueUrl(fullQueueUrl)
                    .messageBody(payment.toString()) // Adjust as needed
                    .build());

        }

        // Return a response
        return ResponseEntity.ok().build();
    }
}