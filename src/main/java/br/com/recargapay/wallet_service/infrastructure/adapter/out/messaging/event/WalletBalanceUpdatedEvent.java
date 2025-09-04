package br.com.recargapay.wallet_service.infrastructure.adapter.out.messaging.event;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class WalletBalanceUpdatedEvent extends BaseEvent implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long walletId;
    private BigDecimal balance;
    private LocalDateTime updatedAt;

    public WalletBalanceUpdatedEvent() {
    }

    public WalletBalanceUpdatedEvent(Long walletId, BigDecimal balance, LocalDateTime updatedAt) {
        this.walletId = walletId;
        this.balance = balance;
        this.updatedAt = updatedAt;
    }

    public Long getWalletId() {
        return walletId;
    }

    public void setWalletId(Long walletId) {
        this.walletId = walletId;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
