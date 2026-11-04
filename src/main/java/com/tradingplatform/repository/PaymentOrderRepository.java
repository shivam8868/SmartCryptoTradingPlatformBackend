package com.tradingplatform.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tradingplatform.modal.PaymentOrder;

public interface PaymentOrderRepository extends JpaRepository<PaymentOrder,Long> {

}
