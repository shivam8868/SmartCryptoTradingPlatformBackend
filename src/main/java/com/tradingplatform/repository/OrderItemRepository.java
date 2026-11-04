package com.tradingplatform.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tradingplatform.modal.OrderItem;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

}
