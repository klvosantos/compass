package com.example.compass.controller;

import com.example.compass.entity.Seller;
import com.example.compass.response.ErrorResponse;
import com.example.compass.service.SellerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/sellers")
public class SellerController {

    private final SellerService sellerService;

    @GetMapping("/hello")
    public String hello() {
        return "Hello, World!";
    }

    @Autowired
    public SellerController(SellerService sellerService) {
        this.sellerService = sellerService;
    }

    @GetMapping
    public ResponseEntity<List<Seller>> getAllSellers() {
        return ResponseEntity.ok(sellerService.getAllSellers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getSellerById(@PathVariable Long id) {
        Seller seller = sellerService.getSellerById(id);
        if (seller != null) {
            return new ResponseEntity<>(seller, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new ErrorResponse("Seller not found"), HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping
    public ResponseEntity<Seller> createSeller(@RequestBody Seller seller) {
        return ResponseEntity.ok(sellerService.saveSeller(seller));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSeller(@PathVariable Long id) {
        sellerService.deleteSeller(id);
        return ResponseEntity.noContent().build();
    }
}