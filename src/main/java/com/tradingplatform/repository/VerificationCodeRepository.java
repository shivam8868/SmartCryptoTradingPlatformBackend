package com.tradingplatform.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tradingplatform.modal.VerificationCode;

public interface VerificationCodeRepository extends JpaRepository<VerificationCode, Long> {
    public VerificationCode findByUserId(Long userId);
}


