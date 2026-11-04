package com.tradingplatform.service;

import com.tradingplatform.modal.TwoFactorOTP;
import com.tradingplatform.modal.User;

// this class is a service for twofactorotp in model and it will contain the business logic for generating and validating OTPs for two factor authentication
public interface TwoFactorServiceOtpService {

    TwoFactorOTP createTwoFactorOtp(User user,String otp,String jwt);

    TwoFactorOTP findByUser(Long userId);
    TwoFactorOTP findById(String id);
    boolean verifyTwoFactorOtp(TwoFactorOTP twoFactorOtp, String otp);

    void deleteTwoFactorOtp(TwoFactorOTP twoFactorOtp);


}
