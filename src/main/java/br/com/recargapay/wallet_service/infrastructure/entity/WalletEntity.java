package br.com.recargapay.wallet_service.infrastructure.entity;

import br.com.recargapay.wallet_service.domain.model.Wallet;
import br.com.recargapay.wallet_service.domain.model.WalletBalanceHistory;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "wallets")
public class WalletEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private BigDecimal balance;

    @OneToMany(mappedBy = "wallet", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<WalletBalanceHistoryEntity> history = new ArrayList<>();

    public WalletEntity() {
    }

    public WalletEntity(Wallet wallet) {
        this.id = wallet.getId();
        this.balance = wallet.getBalance();
        List<WalletBalanceHistoryEntity> historyEntities = wallet.getHistory().stream()
            .map(h -> {
                WalletBalanceHistoryEntity e = new WalletBalanceHistoryEntity();
                e.setWallet(this);
                e.setBalance(h.getBalance());
                e.setUpdatedAt(h.getUpdatedAt());
                return e;
            })
            .toList();
        this.history = historyEntities;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public List<WalletBalanceHistoryEntity> getHistory() {
        return history;
    }

    public void setHistory(List<WalletBalanceHistoryEntity> history) {
        this.history = history;
    }

    public Wallet toDomain() {
        List<WalletBalanceHistory> history = this.history.stream()
            .map(h -> new WalletBalanceHistory(h.getBalance(), h.getUpdatedAt()))
            .toList();
        return new Wallet(this.id, this.balance, history);
    }
}
