package com.tradingplatform.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tradingplatform.modal.PaymentDetails;

public interface PaymentDetailsRepository extends JpaRepository<PaymentDetails, Long>{
    PaymentDetails findByUserId(Long userId);
}
