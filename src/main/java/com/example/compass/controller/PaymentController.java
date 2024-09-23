package com.example.compass.controller;

import com.example.compass.dto.BatchPaymentDTO;
import com.example.compass.dto.PaymentDTO;
import com.example.compass.entity.Payment;
import com.example.compass.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/payments")
public class PaymentController {

    private final PaymentService paymentService;

    @Autowired
    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @GetMapping
    public List<Payment> getAllPayments() {
        return paymentService.getAllPayments();
    }

    @GetMapping("/{id}")
    public Payment getPaymentById(@PathVariable Long id) {
        return paymentService.getPaymentById(id);
    }

    @PostMapping
    public ResponseEntity<Payment> savePayment(@RequestBody PaymentDTO paymentDTO) {
        Payment savedPayment = paymentService.savePayment(paymentDTO);
        return new ResponseEntity<>(savedPayment, HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public void deletePayment(@PathVariable Long id) {
        paymentService.deletePayment(id);
    }

    @PostMapping("/batch")
    public ResponseEntity<?> processBatchPayments(@RequestBody BatchPaymentDTO batchPaymentDTO) {
        try {
            paymentService.processBatchPayments(batchPaymentDTO);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }

    }

}