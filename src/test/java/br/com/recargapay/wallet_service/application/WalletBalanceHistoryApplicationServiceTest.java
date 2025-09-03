package br.com.recargapay.wallet_service.application;

import br.com.recargapay.wallet_service.application.port.out.WalletBalanceHistoryRepositoryPort;
import br.com.recargapay.wallet_service.application.port.out.WalletRepositoryPort;
import br.com.recargapay.wallet_service.application.service.WalletBalanceHistoryApplicationService;
import br.com.recargapay.wallet_service.domain.exception.WalletNotFoundException;
import br.com.recargapay.wallet_service.domain.model.Wallet;
import br.com.recargapay.wallet_service.infrastructure.adapter.in.web.dto.WalletBalanceHistoryRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class WalletBalanceHistoryApplicationServiceTest {

    private WalletRepositoryPort walletRepository;
    private WalletBalanceHistoryRepositoryPort repository;
    private WalletBalanceHistoryApplicationService service;

    @BeforeEach
    void setup() {
        walletRepository = mock(WalletRepositoryPort.class);
        repository = mock(WalletBalanceHistoryRepositoryPort.class);
        service = new WalletBalanceHistoryApplicationService(repository, walletRepository);
    }

    @Test
    void getBalanceAtShouldReturnCorrectHistoricalBalance() throws InterruptedException {
        Wallet wallet = new Wallet(1L, BigDecimal.ZERO);
        wallet.deposit(BigDecimal.valueOf(100));
        LocalDateTime time = LocalDateTime.now();
        Thread.sleep(Duration.ofMillis(1));
        wallet.withdraw(BigDecimal.valueOf(30));

        when(walletRepository.findById(wallet.getId())).thenReturn(Optional.of(wallet));
        when(repository.balanceAt(wallet.getId(), time)).thenReturn(BigDecimal.valueOf(100));

        WalletBalanceHistoryRequest request = new WalletBalanceHistoryRequest();
        request.setBalanceAt(time);
        assertEquals(BigDecimal.valueOf(100), service.getBalanceAt(wallet.getId(), request));
        verify(walletRepository, times(1)).findById(wallet.getId());
        verify(repository, times(1)).balanceAt(wallet.getId(), time);
    }

    @Test
    void getBalanceAtShouldNotFindWallet() {
        assertThrows(WalletNotFoundException.class, () -> service.getBalanceAt(1L, new WalletBalanceHistoryRequest()));
    }
}
