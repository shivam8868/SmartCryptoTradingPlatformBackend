package com.tradingplatform.service;

import com.tradingplatform.domain.VerificationType;
import com.tradingplatform.modal.User;
import com.tradingplatform.modal.VerificationCode;

public interface VerificationCodeService {
    VerificationCode sendVerificationCode(User user,VerificationType verificationType);
    VerificationCode getVerificationCodeById(Long id);
    VerificationCode getVerificationCodeByUser(Long userId);
    void deleteVerificationCodeById(VerificationCode verificationCode);
}
