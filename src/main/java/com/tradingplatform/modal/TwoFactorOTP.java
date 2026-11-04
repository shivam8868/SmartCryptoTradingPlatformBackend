package com.tradingplatform.modal;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import lombok.Data;

@Entity
@Data
public class TwoFactorOTP {

    @Id
    private String id;

    private String otp;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    // one to one relationship with user entity because each user can have only one OTP and each OTP can be associated with only one user
    @OneToOne
    private User user;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Column(columnDefinition  = "TEXT")
    private String jwt;
}
