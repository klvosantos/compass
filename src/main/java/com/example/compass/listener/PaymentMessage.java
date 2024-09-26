package com.example.compass.listener;

import java.util.List;
public record PaymentMessage(Long sellerId, List<Payment> payments) {
    public record Payment(Long id, double amount, Long billingCodeId, double originalAmount, String status) {}
}

