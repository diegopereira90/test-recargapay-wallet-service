package br.com.recargapay.wallet_service.infrastructure.adapter.in.web.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public class WalletTransferRequest {
    @NotNull
    @Positive
    private BigDecimal amount;
    @NotNull
    private Long destinationWalletId;

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Long getDestinationWalletId() {
        return destinationWalletId;
    }

    public void setDestinationWalletId(Long destinationWalletId) {
        this.destinationWalletId = destinationWalletId;
    }
}
