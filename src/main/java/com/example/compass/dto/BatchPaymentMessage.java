package com.example.compass.dto;

import com.example.compass.entity.Payment;

import java.util.List;
import java.util.UUID;

public class BatchPaymentMessage {

    private String id;
    private Long sellerId;
    private List<PaymentDTO> payments;
    public Long getSellerId() {
        return sellerId;
    }

    public BatchPaymentMessage() {
        this.id = UUID.randomUUID().toString(); // Generate a unique ID
    }

    public String getId() {
        return id;
    }

    public void setSellerId(Long sellerId) {
        this.sellerId = sellerId;
    }

    public List<PaymentDTO> getPayments() {
        return payments;
    }

    public void setPayments(List<PaymentDTO> payments) {
        this.payments = payments;
    }

}
