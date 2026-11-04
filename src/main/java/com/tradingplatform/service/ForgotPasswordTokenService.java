package com.tradingplatform.service;

import com.tradingplatform.domain.VerificationType;
import com.tradingplatform.modal.ForgotPasswordToken;
import com.tradingplatform.modal.User;

public interface ForgotPasswordTokenService {

    ForgotPasswordToken createToken(User user,
                                    String id,
                                    String otp,
                                    VerificationType verificationType,
                                    String sendTo
    );      
    ForgotPasswordToken findById(String id);

    ForgotPasswordToken findByUser(Long userId);

    void deleteToken(ForgotPasswordToken token);
}
