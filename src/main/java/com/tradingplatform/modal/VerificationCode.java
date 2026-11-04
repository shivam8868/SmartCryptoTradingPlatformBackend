package com.tradingplatform.modal;

import com.tradingplatform.domain.VerificationType;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import lombok.Data;

@Entity
@Data
public class VerificationCode {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String otp;

    // one to one is used because one user can have only one verification code at a time and one verification code can be associated with only one user at a time. This is to ensure that the verification code is unique for each user and to prevent any confusion or conflicts that may arise if multiple users were to share the same verification code.
    @OneToOne
    private User user;

    private String email;

    private String mobile;

    private VerificationType verificationType;
}
