package com.tradingplatform.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tradingplatform.domain.OrderStatus;
import com.tradingplatform.domain.OrderType;
import com.tradingplatform.modal.Asset;
import com.tradingplatform.modal.Coin;
import com.tradingplatform.modal.Order;
import com.tradingplatform.modal.OrderItem;
import com.tradingplatform.modal.User;
import com.tradingplatform.repository.OrderItemRepository;
import com.tradingplatform.repository.OrderRepository;

import jakarta.transaction.Transactional;
@Service
public class OrderServiceImpl implements OrderService {
    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private WalletService walletService;

    @Autowired
    private OrderItemRepository orderItemRepository;
    @Autowired
    private AssetService assetService;


    @Override
    public Order CreateOrder(User user, OrderItem orderItem, OrderType orderType) {
        double price=orderItem.getCoin().getCurrentPrice()*orderItem.getQuantity();
        Order order=new Order();
        order.setUser(user);
        order.setOrderItem(orderItem);
        order.setOrderType(orderType);
        order.setPrice(BigDecimal.valueOf(price));
        order.setTimestamp(LocalDateTime.now());
        order.setOrderStatus(OrderStatus.PENDING);
        
        return orderRepository.save(order);
    }

    @Override
    public Order getOrderById(Long orderId) {
        return orderRepository.findById(orderId).orElseThrow(() -> new RuntimeException("Order not found"));
    }

    @Override
    public List<Order> getAllOrderOfUser(Long userId, OrderType orderType, String assetSymbol) {
        return orderRepository.findByUserId(userId);
        
    }

    private OrderItem createOrderItem(Coin coin, double quantity, double buyPrice, double sellPrice) {
        OrderItem orderItem = new OrderItem();
        orderItem.setCoin(coin);
        orderItem.setQuantity(quantity);
        orderItem.setBuyPrice(buyPrice);
        orderItem.setSellPrice(sellPrice);
        return orderItemRepository.save(orderItem);

    }
    // here the transactional annotation is used to ensure that the entire process of buying an asset is treated as a single unit of work. If any part of the process fails (e.g., insufficient funds, database errors), the transaction will be rolled back, ensuring data integrity and consistency in the system.
    // means if there is any exception occurs in any step the whole process will not be start from iniaitial stage instead of that the process will be rollback to the state before the transaction began, preventing partial updates to the database and maintaining data integrity.
    @Transactional
    public Order buyAsset(Coin coin, double quantity, User user) {
        if(quantity <=0) {
            throw new IllegalArgumentException("Quantity must be greater than zero");
        }

        double buyPrice = coin.getCurrentPrice();
        OrderItem orderItem = createOrderItem(coin, quantity, buyPrice, 0);

        Order order = CreateOrder(user, orderItem, OrderType.BUY);
        orderItem.setOrder(order);
        walletService.payOrderPayment(order, user);
        order.setOrderStatus(OrderStatus.SUCCESS);
        order.setOrderType(OrderType.BUY);
        Order savedOrder = orderRepository.save(order);

        // create asset 
        Asset oldAsset = assetService.findAssetByUserIdAndCoinId(order.getUser().getId(),order.getOrderItem().getCoin().getId());
        if(oldAsset == null) {
            assetService.createAsset(user,orderItem.getCoin(), orderItem.getQuantity());
        }
        else {
            assetService.updateAsset(oldAsset.getId(), quantity);
        }
        return savedOrder;
    }

        @Transactional
    public Order sellAsset(Coin coin, double quantity, User user) {
        if(quantity <=0) {
            throw new IllegalArgumentException("Quantity must be greater than zero");
        }

        double sellPrice = coin.getCurrentPrice();



        Asset assetToSell = assetService.findAssetByUserIdAndCoinId(user.getId(),coin.getId());
        if(assetToSell != null) {
        double buyPrice = assetToSell.getBuyPrice();
        OrderItem orderItem = createOrderItem(coin, quantity, buyPrice, sellPrice);

        Order order = CreateOrder(user, orderItem, OrderType.SELL);
        orderItem.setOrder(order);

        if(assetToSell.getQuantity() >= quantity) {

            order.setOrderStatus(OrderStatus.SUCCESS);
            order.setOrderType(OrderType.SELL);
            Order savedOrder = orderRepository.save(order);

            walletService.payOrderPayment(order, user);

            Asset updatedAsset = assetService.updateAsset(assetToSell.getId(), -quantity);

            if(updatedAsset.getQuantity()*coin.getCurrentPrice() <=1) {
                assetService.deleteAsset(updatedAsset.getId());
            }
            return savedOrder;
        }
        throw new RuntimeException("Insufficient quantity to sell");
    }
        throw new RuntimeException("Asset not found for selling");

    }

    @Override
    @Transactional
    public Order processOrder(Coin coin, double quantity, OrderType orderType, User user) {
        if(orderType.equals(OrderType.BUY)) {
            return buyAsset(coin, quantity, user);
        } else if(orderType.equals(OrderType.SELL)) {
            return sellAsset(coin, quantity, user);
        }

        throw new IllegalArgumentException("Invalid order type"); 
    }

}
