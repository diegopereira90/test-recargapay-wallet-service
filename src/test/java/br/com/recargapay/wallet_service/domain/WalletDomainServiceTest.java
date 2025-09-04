package br.com.recargapay.wallet_service.domain;

import br.com.recargapay.wallet_service.domain.exception.InsufficientBalanceException;
import br.com.recargapay.wallet_service.domain.exception.InvalidTransferException;
import br.com.recargapay.wallet_service.domain.model.Wallet;
import br.com.recargapay.wallet_service.domain.service.WalletDomainService;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class WalletDomainServiceTest {

    @Test
    void transferShouldMoveMoneyBetweenWallets() {
        WalletDomainService service = new WalletDomainService();
        Wallet from = new Wallet(1L, BigDecimal.valueOf(100));
        Wallet to = new Wallet(2L, BigDecimal.valueOf(50));

        service.transfer(from, to, BigDecimal.valueOf(30));

        assertEquals(BigDecimal.valueOf(70), from.getBalance());
        assertEquals(BigDecimal.valueOf(80), to.getBalance());
        assertEquals(2, from.getEvents().size());
        assertEquals(2, to.getEvents().size());
    }

    @Test
    void transferShouldThrowIfNegativeAmount() {
        WalletDomainService service = new WalletDomainService();
        Wallet from = new Wallet(1L, BigDecimal.valueOf(20));
        Wallet to = new Wallet(2L, BigDecimal.valueOf(50));

        assertThrows(IllegalArgumentException.class, () -> service.transfer(from, to, BigDecimal.valueOf(-10)));
    }

    @Test
    void transferShouldThrowIfInsufficientBalance() {
        WalletDomainService service = new WalletDomainService();
        Wallet from = new Wallet(1L, BigDecimal.valueOf(20));
        Wallet to = new Wallet(2L, BigDecimal.valueOf(50));

        assertThrows(InsufficientBalanceException.class, () -> service.transfer(from, to, BigDecimal.valueOf(30)));
    }

    @Test
    void transferShouldFailWhenTransferringToSameWallet() {
        WalletDomainService service = new WalletDomainService();
        Wallet from = new Wallet(1L, BigDecimal.valueOf(100));
        Wallet to = new Wallet(1L, BigDecimal.valueOf(50));

        InvalidTransferException exception = assertThrows(
            InvalidTransferException.class,
            () -> service.transfer(from, to, BigDecimal.valueOf(50))
        );
    }
}
