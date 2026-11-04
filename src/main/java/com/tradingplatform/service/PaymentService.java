package com.tradingplatform.service;

import com.tradingplatform.domain.PaymentMethod;
import com.tradingplatform.modal.PaymentOrder;
import com.tradingplatform.modal.User;
import com.tradingplatform.response.PaymentResponse;
import com.razorpay.RazorpayException;
import com.stripe.exception.StripeException;

public interface PaymentService {

    PaymentOrder createOrder(User user, Long amount, PaymentMethod paymentMethod);

    PaymentOrder getPaymentOrderById(Long id);

    Boolean proceedPaymentOrder(PaymentOrder paymentOrder, String paymentId) throws RazorpayException;

    PaymentResponse createRazorpayPaymentLink(User user,Long amount, Long orderId) throws RazorpayException;

    PaymentResponse createStripePaymentLink(User user,Long amount, Long OrderId) throws StripeException;
}
