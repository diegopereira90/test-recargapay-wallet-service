package br.com.recargapay.wallet_service.domain;

import br.com.recargapay.wallet_service.domain.exception.InsufficientBalanceException;
import br.com.recargapay.wallet_service.domain.model.Wallet;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class WalletTest {

    @Test
    void createShouldAddHistory() {
        Wallet wallet = new Wallet(1L, BigDecimal.TEN);

        assertEquals(BigDecimal.TEN, wallet.getHistory().getFirst().getBalance());
        assertNotNull(wallet.getHistory().getFirst().getUpdatedAt());
    }

    @Test
    void depositShouldIncreaseBalanceAndAddHistory() {
        Wallet wallet = new Wallet(1L, BigDecimal.TEN);
        wallet.deposit(BigDecimal.ONE);

        assertEquals(BigDecimal.TEN.add(BigDecimal.ONE), wallet.getBalance());
        assertEquals(BigDecimal.TEN.add(BigDecimal.ONE), wallet.getHistory().getLast().getBalance());
        assertNotNull(wallet.getHistory().getLast().getUpdatedAt());
    }

    @Test
    void depositShouldThrowIfNegativeAmount() {
        Wallet wallet = new Wallet(1L, BigDecimal.TEN);

        assertThrows(IllegalArgumentException.class, () -> wallet.deposit(BigDecimal.valueOf(-1)));
    }

    @Test
    void withdrawShouldDecreaseBalanceAndAddHistory() {
        Wallet wallet = new Wallet(1L, BigDecimal.TEN);
        wallet.withdraw(BigDecimal.TWO);

        assertEquals(BigDecimal.TEN.subtract(BigDecimal.TWO), wallet.getBalance());
        assertEquals(BigDecimal.TEN.subtract(BigDecimal.TWO), wallet.getHistory().getLast().getBalance());
        assertNotNull(wallet.getHistory().getLast().getUpdatedAt());
    }

    @Test
    void withdrawShouldThrowIfNegativeAmount() {
        Wallet wallet = new Wallet(1L, BigDecimal.TEN);

        assertThrows(IllegalArgumentException.class, () -> wallet.withdraw(BigDecimal.valueOf(-1)));
    }

    @Test
    void withdrawShouldThrowIfInsufficientBalance() {
        Wallet wallet = new Wallet(1L, BigDecimal.ONE);

        assertThrows(InsufficientBalanceException.class, () -> wallet.withdraw(BigDecimal.TWO));
    }

    @Test
    void getBalanceHistoryShouldReturnCorrect() throws InterruptedException {
        Wallet wallet = new Wallet(1L, BigDecimal.ZERO);
        wallet.deposit(BigDecimal.valueOf(100));
        LocalDateTime time = LocalDateTime.now();
        Thread.sleep(Duration.ofMillis(1));
        wallet.withdraw(BigDecimal.valueOf(30));

        assertEquals(BigDecimal.valueOf(100), wallet.getBalanceAt(time));
    }
}
