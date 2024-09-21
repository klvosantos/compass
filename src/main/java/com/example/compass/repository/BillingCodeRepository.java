package com.example.compass.repository;

import com.example.compass.entity.BillingCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BillingCodeRepository extends JpaRepository<BillingCode, Long> {
}