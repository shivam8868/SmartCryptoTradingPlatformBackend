package com.tradingplatform.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tradingplatform.modal.PaymentOrder;
import com.tradingplatform.modal.User;
import com.tradingplatform.response.PaymentResponse;
import com.tradingplatform.service.PaymentService;
import com.tradingplatform.service.UserService;
import com.razorpay.RazorpayException;
import com.stripe.exception.StripeException;
import com.tradingplatform.domain.PaymentMethod;
// import com.stripe.model.PaymentRecord;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
// import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;


@RestController
@RequestMapping("/api")
public class PaymentController {
    @Autowired
    private UserService userService;

    @Autowired
    private PaymentService paymentService;

    @PostMapping("/api/payment/{paymentMethod}/amount/{amount}")
    public ResponseEntity<PaymentResponse> paymentHandler (
        @PathVariable PaymentMethod paymentMethod,
        @PathVariable Long amount,
        @RequestHeader("Authorization") String jwt)
        throws Exception,RazorpayException,StripeException {
            User user = userService.findUserProfileByJwt(jwt);
            PaymentResponse paymentResponse;

            PaymentOrder order = paymentService.createOrder(user, amount,paymentMethod);     
            
            if(paymentMethod.equals(PaymentMethod.RAZORPAY)) {
                paymentResponse =paymentService.createRazorpayPaymentLink(user, amount, order.getId());
            }
            else  {
                paymentResponse = paymentService.createStripePaymentLink(user, amount, order.getId());
                
            }

            return new ResponseEntity<>(paymentResponse,HttpStatus.CREATED);
        }
}