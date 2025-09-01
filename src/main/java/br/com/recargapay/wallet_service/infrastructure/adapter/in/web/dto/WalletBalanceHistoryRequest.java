package br.com.recargapay.wallet_service.infrastructure.adapter.in.web.dto;

import br.com.recargapay.wallet_service.infrastructure.adapter.in.web.validation.NotFuture;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public class WalletBalanceHistoryRequest {
    @NotNull
    @NotFuture
    private LocalDateTime balanceAt;

    public LocalDateTime getBalanceAt() {
        return balanceAt;
    }

    public void setBalanceAt(LocalDateTime balanceAt) {
        this.balanceAt = balanceAt;
    }
}
