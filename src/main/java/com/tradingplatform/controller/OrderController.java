package com.tradingplatform.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tradingplatform.domain.OrderType;
import com.tradingplatform.modal.Coin;
import com.tradingplatform.modal.Order;
import com.tradingplatform.modal.User;
import com.tradingplatform.request.CreateOrderRequest;
import com.tradingplatform.service.CoinService;
import com.tradingplatform.service.OrderService;
import com.tradingplatform.service.UserService;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    @Autowired
    private OrderService orderService;

    @Autowired
    private UserService userService;

    @Autowired
    private CoinService coinService;
    

    // @Autowired
    // private WalleteTransactionService walletTransactionService;

    @PostMapping("/pay")
    public ResponseEntity<Order> payOrderPayment(@RequestHeader("Authorization") String jwt,@RequestBody CreateOrderRequest req) throws Exception {
        User user = userService.findUserProfileByJwt(jwt);
        Coin coin = coinService.findById(req.getCoinId());


        Order order = orderService.processOrder(coin, req.getQuantity(), req.getOrderType(), user);
        return ResponseEntity.ok(order);
    }


    @GetMapping("/{orderId}")
    public ResponseEntity<Order> getOrderById(
        @RequestHeader("Authorization") String jwtToken,
        @PathVariable Long orderId
    ) throws Exception {
        User user = userService.findUserProfileByJwt(jwtToken);
        Order order = orderService.getOrderById(orderId);
        if(order.getUser().getId().equals(user.getId())) {
            return ResponseEntity.ok(order); // Forbidden
        }
        else {
            throw new Exception("You are not authorized to access this order");
        }
    }    

    @GetMapping
    public ResponseEntity<List<Order>> getAllOrderForUsesr(
            @RequestHeader("Authorization") String jwtToken,
            @RequestParam(required = false) OrderType order_type,
            @RequestParam(required = false) String asset_symbol
    ) throws Exception {
        Long userId = userService.findUserProfileByJwt(jwtToken).getId();

        List<Order> userOrders = orderService.getAllOrderOfUser(userId, order_type, asset_symbol);
        return ResponseEntity.ok(userOrders);
    }
}
