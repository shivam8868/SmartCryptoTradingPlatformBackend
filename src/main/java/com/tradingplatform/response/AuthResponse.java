package com.tradingplatform.response;

import lombok.Data;

@Data
public class AuthResponse {
    private String jwt;
    private String status;
    private String message;

    private boolean isTwoFactorAuthEnabled;
    private String session;
}
