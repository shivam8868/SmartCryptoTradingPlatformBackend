package com.tradingplatform.request;

import com.tradingplatform.domain.OrderType;

import lombok.Data;

@Data
public class CreateOrderRequest {
    private String coinId;
    private double quantity;
    private OrderType orderType;
    
}
