package com.example.compass.controller;

import com.example.compass.entity.BillingCode;
import com.example.compass.service.BillingCodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/billing-codes")
public class BillingCodeController {

    private final BillingCodeService billingCodeService;

    @Autowired
    public BillingCodeController(BillingCodeService billingCodeService) {
        this.billingCodeService = billingCodeService;
    }

    @GetMapping
    public List<BillingCode> getAllBillingCodes() {
        return billingCodeService.getAllBillingCodes();
    }

    @GetMapping("/{id}")
    public BillingCode getBillingCodeById(@PathVariable Long id) {
        return billingCodeService.getBillingCodeById(id);
    }

    @PostMapping
    public BillingCode saveBillingCode(@RequestBody BillingCode billingCode) {
        return billingCodeService.saveBillingCode(billingCode);
    }

    @DeleteMapping("/{id}")
    public void deleteBillingCode(@PathVariable Long id) {
        billingCodeService.deleteBillingCode(id);
    }
}