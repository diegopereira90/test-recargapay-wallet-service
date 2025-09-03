package br.com.recargapay.wallet_service.domain;

import br.com.recargapay.wallet_service.domain.exception.InsufficientBalanceException;
import br.com.recargapay.wallet_service.domain.model.Wallet;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class WalletTest {

    @Test
    void createShouldAddEvent() {
        Wallet wallet = new Wallet(1L, BigDecimal.TEN);

        assertEquals(BigDecimal.TEN, wallet.getEvents().getFirst().getBalance());
        assertNotNull(wallet.getEvents().getFirst().getUpdatedAt());
    }

    @Test
    void depositShouldIncreaseBalanceAndAddEvent() {
        Wallet wallet = new Wallet(1L, BigDecimal.TEN);
        wallet.deposit(BigDecimal.ONE);

        assertEquals(BigDecimal.TEN.add(BigDecimal.ONE), wallet.getBalance());
        assertEquals(BigDecimal.TEN.add(BigDecimal.ONE), wallet.getEvents().getLast().getBalance());
        assertNotNull(wallet.getEvents().getLast().getUpdatedAt());
    }

    @Test
    void depositShouldThrowIfNegativeAmount() {
        Wallet wallet = new Wallet(1L, BigDecimal.TEN);

        assertThrows(IllegalArgumentException.class, () -> wallet.deposit(BigDecimal.valueOf(-1)));
    }

    @Test
    void withdrawShouldDecreaseBalanceAndAddEvent() {
        Wallet wallet = new Wallet(1L, BigDecimal.TEN);
        wallet.withdraw(BigDecimal.TWO);

        assertEquals(BigDecimal.TEN.subtract(BigDecimal.TWO), wallet.getBalance());
        assertEquals(BigDecimal.TEN.subtract(BigDecimal.TWO), wallet.getEvents().getLast().getBalance());
        assertNotNull(wallet.getEvents().getLast().getUpdatedAt());
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
}
