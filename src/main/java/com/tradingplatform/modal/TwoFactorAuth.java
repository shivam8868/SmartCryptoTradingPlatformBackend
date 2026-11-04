package com.tradingplatform.modal;

import com.tradingplatform.domain.VerificationType;

import lombok.Data;

@Data
public class TwoFactorAuth {

    private boolean isEnabled=false;
    private VerificationType sendTo;
}
