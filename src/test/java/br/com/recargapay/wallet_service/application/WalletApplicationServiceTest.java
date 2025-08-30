package br.com.recargapay.wallet_service.application;

import br.com.recargapay.wallet_service.application.dto.HistoricalBalanceRequest;
import br.com.recargapay.wallet_service.application.dto.WalletCreateRequest;
import br.com.recargapay.wallet_service.application.dto.WalletResponse;
import br.com.recargapay.wallet_service.application.service.WalletApplicationService;
import br.com.recargapay.wallet_service.domain.model.Wallet;
import br.com.recargapay.wallet_service.domain.service.WalletDomainService;
import br.com.recargapay.wallet_service.ports.out.WalletRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class WalletApplicationServiceTest {

    private WalletRepositoryPort repository;
    private WalletApplicationService service;

    @BeforeEach
    void setup() {
        repository = mock(WalletRepositoryPort.class);
        WalletDomainService domainService = new WalletDomainService();
        service = new WalletApplicationService(repository, domainService);
    }

    @Test
    void createWalletShouldReturnWalletResponse() {
        Wallet wallet = new Wallet(1L, BigDecimal.valueOf(100));
        Wallet saved = new Wallet(1L, BigDecimal.valueOf(100));

        when(repository.save(any(Wallet.class))).thenReturn(saved);

        WalletResponse response = service.createWallet(new WalletCreateRequest(BigDecimal.valueOf(100)));

        assertEquals(1L, response.walletId());
        verify(repository, times(1)).save(any(Wallet.class));
    }

    @Test
    void depositShouldUpdateWalletBalance() {
        Wallet wallet = new Wallet(1L, BigDecimal.valueOf(100));
        when(repository.findById(1L)).thenReturn(Optional.of(wallet));

        service.deposit(1L, BigDecimal.valueOf(50));

        assertEquals(BigDecimal.valueOf(150), wallet.getBalance());
        verify(repository, times(1)).save(wallet);
    }

    @Test
    void withdrawShouldUpdateWalletBalance() {
        Wallet wallet = new Wallet(1L, BigDecimal.valueOf(100));
        when(repository.findById(1L)).thenReturn(Optional.of(wallet));

        service.withdraw(1L, BigDecimal.valueOf(30));

        assertEquals(BigDecimal.valueOf(70), wallet.getBalance());
        verify(repository, times(1)).save(wallet);
    }

    @Test
    void getBalanceShouldReturnCorrectBalance() {
        Wallet wallet = new Wallet(1L, BigDecimal.TEN);
        when(repository.findById(1L)).thenReturn(Optional.of(wallet));

        assertEquals(service.getBalance(1L), wallet.getBalance());
        verify(repository, times(1)).findById(1L);
    }

    @Test
    void getBalanceAtShouldReturnCorrectHistoricalBalance() throws InterruptedException {
        Wallet wallet = new Wallet(1L, BigDecimal.ZERO);
        wallet.deposit(BigDecimal.valueOf(100));
        LocalDateTime time = LocalDateTime.now();
        Thread.sleep(Duration.ofMillis(1));
        wallet.withdraw(BigDecimal.valueOf(30));

        when(repository.findById(1L)).thenReturn(Optional.of(wallet));

        HistoricalBalanceRequest request = new HistoricalBalanceRequest(1L, time);
        assertEquals(BigDecimal.valueOf(100), service.getHistoricalBalance(request));
        verify(repository, times(1)).findById(1L);
    }
}
