package com.tradingplatform.service;

import java.util.List;

import com.tradingplatform.domain.OrderType;
import com.tradingplatform.modal.Coin;
import com.tradingplatform.modal.Order;
import com.tradingplatform.modal.OrderItem;
import com.tradingplatform.modal.User;

public interface OrderService {

    Order CreateOrder(User user, OrderItem orderItem, OrderType orderType);

    Order getOrderById(Long orderId);

    List<Order> getAllOrderOfUser(Long userId, OrderType orderType, String assetSymbol);

    Order processOrder(Coin coin, double quantity, OrderType orderType, User user);

    
}
