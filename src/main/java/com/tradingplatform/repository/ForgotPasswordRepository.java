package com.tradingplatform.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tradingplatform.modal.ForgotPasswordToken;

public interface ForgotPasswordRepository extends JpaRepository<ForgotPasswordToken, String> {
    // ForgotPasswordToken findById(String id);

    // ForgotPasswordToken findByUser(Long userId);

    // void deleteById(String token);

    ForgotPasswordToken findByUserId(Long userId);


}
