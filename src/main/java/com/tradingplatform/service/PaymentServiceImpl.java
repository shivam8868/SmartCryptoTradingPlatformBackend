package com.tradingplatform.service;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.tradingplatform.domain.PaymentMethod;
import com.tradingplatform.domain.PaymentOrderStatus;
import com.tradingplatform.modal.PaymentOrder;
import com.tradingplatform.modal.User;
import com.tradingplatform.repository.PaymentOrderRepository;
import com.tradingplatform.response.PaymentResponse;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
// import com.stripe.model.checkout.SessionCollection;
// import com.stripe.param.billingportal.SessionCreateParams;
import com.stripe.param.checkout.SessionCreateParams;

// import com.razorpay.Payment;
import com.razorpay.PaymentLink;

@Service
public class PaymentServiceImpl implements PaymentService{

    @Autowired
    private PaymentOrderRepository paymentOrderRepository;

    @Value("${stripe.secret.key}")
    private String stripeSecretKey;
    @Value("${razorpay.api.key}")
    private String apiKey;
    @Value("${razorpay.api.secret}")
    private String apiSecretKey;
    @Override
    public PaymentOrder createOrder(User user, Long amount, PaymentMethod paymentMethod) {
       PaymentOrder paymentOrder = new PaymentOrder();
       paymentOrder.setUser(user);
       paymentOrder.setAmount(amount);
       paymentOrder.setPaymentMethod(paymentMethod);
       paymentOrder.setPaymentOrderStatus(PaymentOrderStatus.PENDING);
       
        return paymentOrderRepository.save(paymentOrder);
    }

    @Override
    public PaymentOrder getPaymentOrderById(Long id) {
        return paymentOrderRepository.findById(id).orElseThrow(()->new RuntimeException("payement order not found"));
    }

    // @Override
    // public Boolean proceedPaymentOrder(PaymentOrder paymentOrder, String paymentId) throws RazorpayException {
    //     if(paymentOrder.getPaymentOrderStatus()==null) {
    //         paymentOrder.setPaymentOrderStatus(PaymentOrderStatus.PENDING);
    //     }
        
    //     if(paymentOrder.getPaymentOrderStatus().equals(PaymentOrderStatus.PENDING)) {
    //         if (paymentOrder.getPaymentMethod().equals(PaymentMethod.RAZORPAY)) {
    //             RazorpayClient razorpay=new RazorpayClient(apiKey,apiSecretKey);
    //             Payment payment = razorpay.payments.fetch(paymentId);

    //             Integer amount = payment.get("amount");
    //             String status = payment.get("status");
    //             System.out.println("PaymentId: " + paymentId);
    //             System.out.println("Payment status from Razorpay: " + status);
    //             System.out.println("Payment amount from Razorpay: " + amount);
    //             System.out.println("Payment status form razorpay "+ status);
    //             if(status.equals("captured") || "authorized".equals(status)) {
    //                 paymentOrder.setPaymentOrderStatus(PaymentOrderStatus.SUCCESS);
    //                 paymentOrderRepository.save(paymentOrder);
    //                 return true;
    //             }
    //             else {
    //                 paymentOrder.setPaymentOrderStatus(PaymentOrderStatus.FAILED);
    //                 paymentOrderRepository.save(paymentOrder);
    //                 return false;
    //             }
    //         }

    //         // stripe or other methods 
    //         paymentOrder.setPaymentOrderStatus(PaymentOrderStatus.SUCCESS);
    //         paymentOrderRepository.save(paymentOrder);
    //         return true;
    //     } 
        
    //     return false;
    // }

    @Override
    public Boolean proceedPaymentOrder(PaymentOrder paymentOrder, String paymentId) {
    System.out.println("Skipping Razorpay verification for testing. PaymentId: " + paymentId);
    paymentOrder.setPaymentOrderStatus(PaymentOrderStatus.SUCCESS);
    paymentOrderRepository.save(paymentOrder);
    return true;
}


    @Override
    public PaymentResponse createRazorpayPaymentLink(User user, Long amount, Long orderId) throws RazorpayException {
            Long Amount = amount*100;
        try {
            RazorpayClient razorpay = new RazorpayClient(apiKey,apiSecretKey);

            JSONObject paymentLinkRequest = new JSONObject();
            paymentLinkRequest.put("amount",Amount); // i think Amount will be here 
            paymentLinkRequest.put("currency","USD");  //   using usd insted of inr here 


            JSONObject customer = new JSONObject();
            customer.put("name", user.getFullName());

            customer.put("email",user.getEmail());
            paymentLinkRequest.put("customer",customer);

            JSONObject notify = new JSONObject();
            notify.put("email",true);
            paymentLinkRequest.put("notify",notify);

            // set reminder settings
            paymentLinkRequest.put("reminder_enable",true);

            // set callback url and methhod
            // paymentLinkRequest.put("callback_url","http://localhost:9898/wallet?order_id="+orderId);
            paymentLinkRequest.put("callback_url","http://localhost:5173/wallet?order_id="+orderId);

            paymentLinkRequest.put("callback_method","get");

            // create a payment link usinng the paymentlink.create() method 
            PaymentLink payment = razorpay.paymentLink.create(paymentLinkRequest);

            String paymentLinkId = payment.get("id");
            String paymentLinkUrl = payment.get("short_url");

            PaymentResponse res=new PaymentResponse();
            res.setPaymentUrl(paymentLinkUrl);

            return res;
        }catch(RazorpayException e) {
            System.out.println("error creating papymentLInk :"+e.getMessage());
            throw new RazorpayException(e.getMessage());
        }
        
            

    }

    @Override
    public PaymentResponse createStripePaymentLink(User user, Long amount, Long OrderId) throws StripeException {
        Stripe.apiKey = stripeSecretKey;
        
        SessionCreateParams params = SessionCreateParams.builder()
                .addPaymentMethodType(SessionCreateParams.PaymentMethodType.CARD)
                .setMode(SessionCreateParams.Mode.PAYMENT)
                // .setSuccessUrl("http://localhost:9898/wallet?order_id="+OrderId)
                .setSuccessUrl("http://localhost:5173/wallet?order_id="+OrderId + "&session_id={CHECKOUT_SESSION_ID}")

                .setCancelUrl("http://localhost:9898/payment/cancel")
                .addLineItem(SessionCreateParams.LineItem.builder()
                    .setQuantity(1L)
                    .setPriceData(
                            SessionCreateParams.LineItem.PriceData.builder()
                            .setCurrency("usd")
                            .setUnitAmount(amount*100)
                            .setProductData(
                                    SessionCreateParams
                                    .LineItem
                                    .PriceData
                                    .ProductData
                                    .builder()
                                    .setName("Top up wallet")
                                    .build()
                            ).build()
                    ).build()
                ).build();
                    
        Session session = Session.create(params);

        System.out.println("session _____" + session);

        PaymentResponse res = new PaymentResponse();

        res.setPaymentUrl(session.getUrl());

        return res;
            
    }

}
