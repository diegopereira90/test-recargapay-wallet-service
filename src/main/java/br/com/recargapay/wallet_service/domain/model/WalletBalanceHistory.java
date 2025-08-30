package br.com.recargapay.wallet_service.domain.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class WalletBalanceHistory {

    private final BigDecimal balance;
    private final LocalDateTime updatedAt;

    public WalletBalanceHistory(BigDecimal balance, LocalDateTime updatedAt) {
        this.balance = balance;
        this.updatedAt = updatedAt;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
