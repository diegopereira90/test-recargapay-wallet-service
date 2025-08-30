package br.com.recargapay.wallet_service.domain.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Wallet {

    private final Long id;
    private final List<WalletBalanceHistory> history = new ArrayList<>();
    private BigDecimal balance;

    public Wallet(Long id, BigDecimal initialBalance) {
        this.id = id;
        this.balance = initialBalance;
        addHistory(initialBalance);
    }

    public Wallet(Long id, BigDecimal balance, List<WalletBalanceHistory> history) {
        this.id = id;
        this.balance = balance;
        this.history.addAll(history);
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public List<WalletBalanceHistory> getHistory() {
        return Collections.unmodifiableList(history);
    }

    public Long getId() {
        return id;
    }

    public void deposit(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
        balance = balance.add(amount);
        addHistory(balance);
    }

    public void withdraw(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
        if (balance.compareTo(amount) < 0) {
            throw new IllegalStateException("Insufficient balance");
        }
        balance = balance.subtract(amount);
        addHistory(balance);
    }

    public BigDecimal getBalanceAt(LocalDateTime updatedAt) {
        return history.stream()
            .sorted(Comparator.comparing(WalletBalanceHistory::getUpdatedAt))
            .filter(h -> !h.getUpdatedAt().truncatedTo(ChronoUnit.MILLIS).isAfter(updatedAt))
            .reduce((first, second) -> second)
            .map(WalletBalanceHistory::getBalance)
            .orElse(BigDecimal.ZERO);
    }

    private void addHistory(BigDecimal balance) {
        history.add(new WalletBalanceHistory(balance, LocalDateTime.now()));
    }
}
