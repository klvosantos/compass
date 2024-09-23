package com.example.compass.dto;

import java.io.Serializable;
import java.math.BigDecimal;

public class PaymentDTO implements Serializable {

    private Long id;

    private BigDecimal amount;

    private Long billingCodeId;

    private double originalAmount;

    private String status = "NOT_PROCESSED";

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Long getBillingCodeId() { // new getter
        return billingCodeId;
    }

    public void setBillingCodeId(Long billingCodeId) { // new setter
        this.billingCodeId = billingCodeId;
    }

    public double getOriginalAmount() {
        return originalAmount;
    }

    public String getStatus() { // New getter
        return status;
    }

    public void setStatus(String status) { // New setter
        this.status = status;
    }
}