package com.tradingplatform.repository;

import com.tradingplatform.modal.TwoFactorOTP;
import org.springframework.data.jpa.repository.JpaRepository;
public interface TwoFactorOtpRepository extends JpaRepository<TwoFactorOTP, String> {
    TwoFactorOTP findByUserId(Long userId);
}
