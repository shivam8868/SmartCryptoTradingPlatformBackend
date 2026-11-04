package com.tradingplatform.service;

import com.tradingplatform.modal.PaymentDetails;
import com.tradingplatform.modal.User;

public interface PaymentDetailsService {
    public PaymentDetails addPaymentDetails(String accountNumber, String accountHolderName,String ifsc,String bankName,User user);

    public PaymentDetails getUsersPaymentDetails(User user);
}
