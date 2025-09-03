package br.com.recargapay.wallet_service.domain.model;

import br.com.recargapay.wallet_service.domain.exception.InsufficientBalanceException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Wallet {

    private final List<WalletBalanceChanged> events = new ArrayList<>();
    private Long id;
    private BigDecimal balance;

    public Wallet(Long id, BigDecimal balance) {
        this(id, balance, true);
    }

    public Wallet(Long id, BigDecimal balance, Boolean withEvent) {
        this.id = id;
        this.balance = balance;
        if (withEvent) {
            addEvent(balance);
        }
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public List<WalletBalanceChanged> getEvents() {
        return Collections.unmodifiableList(events);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void deposit(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be positive for deposit");
        }
        balance = balance.add(amount);
        addEvent(balance);
    }

    public void withdraw(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be positive for withdraw");
        }
        if (balance.compareTo(amount) < 0) {
            throw new InsufficientBalanceException("Insufficient balance for withdraw/transfer");
        }
        balance = balance.subtract(amount);
        addEvent(balance);
    }

    private void addEvent(BigDecimal balance) {
        events.add(new WalletBalanceChanged(balance, LocalDateTime.now()));
    }

    public void clearEvents() {
        events.clear();
    }
}
