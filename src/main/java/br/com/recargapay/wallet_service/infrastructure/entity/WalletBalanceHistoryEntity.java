package br.com.recargapay.wallet_service.infrastructure.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "wallet_balance_history")
public class WalletBalanceHistoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private BigDecimal balance;
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "wallet_id")
    private WalletEntity wallet;

    public Long getId() { return id; }
    public BigDecimal getBalance() { return balance; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public WalletEntity getWallet() { return wallet; }
    public void setBalance(BigDecimal balance) { this.balance = balance; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    public void setWallet(WalletEntity wallet) { this.wallet = wallet; }
}
