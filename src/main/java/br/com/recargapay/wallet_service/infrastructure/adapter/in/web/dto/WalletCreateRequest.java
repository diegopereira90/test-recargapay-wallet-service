package br.com.recargapay.wallet_service.infrastructure.adapter.in.web.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public class WalletCreateRequest {
    @NotNull
    @Positive
    private BigDecimal initialAmount;

    public BigDecimal getInitialAmount() {
        return initialAmount;
    }

    public void setInitialAmount(BigDecimal initialAmount) {
        this.initialAmount = initialAmount;
    }
}
