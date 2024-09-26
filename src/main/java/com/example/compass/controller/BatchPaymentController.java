package com.example.compass.controller;

import com.example.compass.dto.BatchPaymentDTO;
import com.example.compass.dto.BatchPaymentMessage;
import com.example.compass.dto.PaymentDTO;
import com.example.compass.listener.SqsProducer;
import com.example.compass.service.BillingCodeService;
import com.example.compass.service.SellerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
public class BatchPaymentController {
    private static final Logger log = LoggerFactory.getLogger(BatchPaymentController.class);
    private final SellerService sellerService;
    private final BillingCodeService billingCodeService;

    @Value("${sqs.partial.queue}")
    private String SQSPartial;

    @Value("${sqs.full.queue}")
    private String SQSFull;

    @Value("${sqs.excess.queue}")
    private String SQSExcess;

    @Autowired
    private final SqsProducer sqsProducer;

    @Autowired
    public BatchPaymentController(SellerService sellerService, BillingCodeService billingCodeService, SqsProducer sqsProducer) {
        this.sellerService = sellerService;
        this.billingCodeService = billingCodeService;
        this.sqsProducer = sqsProducer;
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

        // Create a new BatchPaymentMessage to send to SQS
        BatchPaymentMessage batchPaymentMessage = new BatchPaymentMessage();
        batchPaymentMessage.setSellerId(sellerId);
        batchPaymentMessage.setPayments(payments);

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
                payment.setStatus("PARTIAL");
                log.info("Sending partial payment message...");
                sqsProducer.sendMessage(batchPaymentMessage);
            } else if (paidAmount.compareTo(originalAmount) == 0) {
                // The payment is full
                payment.setStatus("FULL");
                log.info("Sending full payment message...");
                sqsProducer.sendMessage(batchPaymentMessage);
            } else {
                // The payment is excess
                payment.setStatus("OVERPAID");
                log.info("Sending excess payment message...");
                sqsProducer.sendMessage(batchPaymentMessage);
            }

        }

        return ResponseEntity.ok(batchPayment);
    }
}