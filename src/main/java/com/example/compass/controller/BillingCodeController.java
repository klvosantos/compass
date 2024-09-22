package com.example.compass.controller;

import com.example.compass.entity.BillingCode;
import com.example.compass.response.ErrorResponse;
import com.example.compass.service.BillingCodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

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
    public ResponseEntity<?> getBillingCodeById(@PathVariable Long id) {
        Optional<BillingCode> billingCodeOptional = billingCodeService.getBillingCodeById(id);
        if (billingCodeOptional.isPresent()) {
            return new ResponseEntity<>(billingCodeOptional.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new ErrorResponse("Billing code not found"), HttpStatus.NOT_FOUND);
        }
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