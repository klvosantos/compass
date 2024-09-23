package com.example.compass.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private BigDecimal amount;

    private Long billingCodeId;
    @Enumerated(EnumType.STRING)
    private PaymentStatus status = PaymentStatus.NOT_PROCESSED;;

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

    public Long getBillingCodeId() {
        return billingCodeId;
    }

    public void setBillingCodeId(Long billingCodeId) {
        this.billingCodeId = billingCodeId;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public void setStatus(PaymentStatus status) {
        this.status = status;
    }

    // Enum for payment status
    public enum PaymentStatus {
        NOT_PROCESSED,
        PARTIAL,
        FULL,
        OVERPAID
    }
}