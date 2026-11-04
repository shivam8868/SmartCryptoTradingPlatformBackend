package com.tradingplatform.request;

import com.tradingplatform.domain.VerificationType;

import lombok.Data;

@Data
public class ForgotPasswordTokenRequest {
    private String SendTo;
    private VerificationType verificationType;
}
