package com.example.compass.dto;

import java.util.List;

public class BatchPaymentDTO {

    private Long sellerId;
    private List<PaymentDTO> payments;

    public Long getSellerId() {
        return sellerId;
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