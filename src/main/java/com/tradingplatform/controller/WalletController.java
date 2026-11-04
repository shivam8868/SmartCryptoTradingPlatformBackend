package com.tradingplatform.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
// import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tradingplatform.modal.Order;
import com.tradingplatform.modal.PaymentOrder;
import com.tradingplatform.modal.User;
import com.tradingplatform.modal.Wallet;
import com.tradingplatform.modal.WalletTransaction;
// import com.tradingplatform.response.PaymentResponse;
import com.tradingplatform.service.OrderService;
import com.tradingplatform.service.PaymentService;
import com.tradingplatform.service.UserService;
import com.tradingplatform.service.WalletService;

import jakarta.servlet.http.HttpServletResponse;

@RestController
// @RequestMapping("/api/wallet")
public class WalletController {
    @Autowired
    private WalletService walletService;

    @Autowired
    private UserService userService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private PaymentService paymentService;

    @GetMapping("/api/wallet")
    public ResponseEntity<Wallet> getUserWallet(@RequestHeader("Authorization") String jwt) throws Exception {
        User user = userService.findUserProfileByJwt(jwt);
        Wallet wallet =  walletService.getUserWallet(user);

        return new ResponseEntity<>(wallet,HttpStatus.ACCEPTED);  
    }

    @PutMapping("/api/wallet/{walletId}/transfer")
    public ResponseEntity<Wallet> walletToWalletTransfer(@RequestHeader("Authorization") String jwt,@PathVariable Long walletId, @RequestBody WalletTransaction req ) throws Exception {
         User senderUser = userService.findUserProfileByJwt(jwt);
         Wallet receiverWallet = walletService.findWalletById(walletId);
         Wallet wallet= walletService.walletToWalletTransfer(senderUser, receiverWallet, req.getAmount());
         return new ResponseEntity<>(wallet, HttpStatus.ACCEPTED);
    }

    @PutMapping("/api/wallet/order/{orderId}/pay")
    public ResponseEntity<Wallet> payOrderPayment(@RequestHeader("Authorization") String jwt,@PathVariable Long orderId) throws Exception {
         User user = userService.findUserProfileByJwt(jwt);
        Order order=orderService.getOrderById(orderId);
        Wallet wallet = walletService.payOrderPayment(order, user);
         return new ResponseEntity<>(wallet, HttpStatus.ACCEPTED);
    }
    
    // @PutMapping("/api/wallet/deposit")
    // public ResponseEntity<Wallet> addBalanceToWallet(@RequestHeader("Authorization") String jwt,@RequestParam(name= "order_id") Long orderId, @RequestParam(name="payment_id") String paymentId) throws Exception {
    //      User user = userService.findUserProfileByJwt(jwt);
    //     Wallet wallet = walletService.getUserWallet(user);

    //     PaymentOrder order = paymentService.getPaymentOrderById(orderId);
    //     Boolean status = paymentService.proceedPaymentOrder(order, paymentId);
    //     if(status) {
    //         wallet=walletService.addBalance(wallet, order.getAmount());
    //     }
    //      return new ResponseEntity<>(wallet, HttpStatus.ACCEPTED);
    // }

@PutMapping("/api/wallet/deposit")
public ResponseEntity<Wallet> addBalanceToWallet(
        @RequestHeader("Authorization") String jwt,
        @RequestParam(name= "order_id") Long orderId,
        @RequestParam(name="payment_id", required = false) String paymentId) throws Exception {

    User user = userService.findUserProfileByJwt(jwt); // 👈 correct method
    Wallet wallet = walletService.getUserWallet(user);

    PaymentOrder order = paymentService.getPaymentOrderById(orderId);
    if(order == null) {
        throw new Exception("Payment order not found");
    }

    Boolean status = paymentService.proceedPaymentOrder(order, paymentId);
    if(status) {
        wallet = walletService.addBalance(wallet, order.getAmount());
    } else {
        throw new Exception("Payment verification failed");
    }

    return new ResponseEntity<>(wallet, HttpStatus.ACCEPTED);
}

// @GetMapping("/api/wallet/callback")
// public ResponseEntity<?> razorpayCallback(
//         @RequestParam(name = "order_id") Long orderId,
//         @RequestParam(name = "razorpay_payment_id", required = false) String razorpayPaymentId,
//         @RequestParam(name = "razorpay_payment_link_id", required = false) String razorpayPaymentLinkId,
//         @RequestHeader("Authorization") String jwt) throws Exception {

//     User user = userService.findUserProfileByJwt(jwt);
//     Wallet wallet = walletService.getUserWallet(user);

//     PaymentOrder order = paymentService.getPaymentOrderById(orderId);
//     if (order == null) {
//         return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Payment order not found");
//     }

//     Boolean status = paymentService.proceedPaymentOrder(order, razorpayPaymentId);
//     if (status) {
//         wallet = walletService.addBalance(wallet, order.getAmount());
//         // ✅ Redirect user back to frontend wallet page
//         return ResponseEntity.status(HttpStatus.FOUND)
//                 .header("Location", "http://localhost:5173/wallet?order_id=" + orderId + "&razorpay_payment_id=" + razorpayPaymentId)
//                 .build();
//     } else {
//         return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Payment verification failed");
//     }
// }

// @GetMapping("/api/wallet/callback")
// public ResponseEntity<Void> razorpayCallback(
//         @RequestParam(name = "order_id") Long orderId,
//         @RequestParam(name = "razorpay_payment_id", required = false) String razorpayPaymentId,
//         @RequestParam(name = "razorpay_payment_link_id", required = false) String razorpayPaymentLinkId) throws Exception {

//     PaymentOrder order = paymentService.getPaymentOrderById(orderId);
//     if (order == null) {
//         return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
//     }

//     Boolean status = paymentService.proceedPaymentOrder(order, razorpayPaymentId);
//     if (status) {
//         walletService.addBalance(walletService.getUserWallet(order.getUser()), order.getAmount());
//         // ✅ Redirect user back to frontend wallet page with params
//         return ResponseEntity.status(HttpStatus.FOUND)
//                 .header("Location", "http://localhost:5173/wallet?order_id=" + orderId + "&razorpay_payment_id=" + razorpayPaymentId)
//                 .build();
//     } else {
//         return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
//     }
// }
@GetMapping("/api/wallet/callback")
public void razorpayCallback(
        @RequestParam(name = "order_id") Long orderId,
        @RequestParam(name = "razorpay_payment_id") String razorpayPaymentId,
        HttpServletResponse response) throws Exception {

    PaymentOrder order = paymentService.getPaymentOrderById(orderId);
    if (order != null && paymentService.proceedPaymentOrder(order, razorpayPaymentId)) {
        walletService.addBalance(walletService.getUserWallet(order.getUser()), order.getAmount());
        // ✅ Redirect to frontend wallet page with params
        response.sendRedirect("http://localhost:5173/wallet?order_id=" + orderId + "&razorpay_payment_id=" + razorpayPaymentId);
    } else {
        response.sendRedirect("http://localhost:5173/wallet?error=payment_failed");
    }
}



}
