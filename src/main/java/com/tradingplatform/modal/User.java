package com.tradingplatform.modal;

import com.tradingplatform.domain.AccountStatus;
import com.tradingplatform.domain.USER_ROLE;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String fullName;
    private String email;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    @Embedded
    private TwoFactorAuth twoFactorAuth = new TwoFactorAuth();

    @Enumerated(EnumType.STRING)
    private USER_ROLE role = USER_ROLE.ROLE_CUSTOMER;

    @Enumerated(EnumType.STRING)
    private AccountStatus accountStatus=AccountStatus.INACTIVE;
    // methoods for useprofiled etails 
    private String phoneNumber;
    private String address;
    private String city;
    private String state;
    private String country;
    private String bio;
    private String profileImage;

}
