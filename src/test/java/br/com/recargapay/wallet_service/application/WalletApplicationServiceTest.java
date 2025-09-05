package br.com.recargapay.wallet_service.application;

import br.com.recargapay.wallet_service.application.port.out.WalletBalanceEventMessagingPort;
import br.com.recargapay.wallet_service.application.port.out.WalletRepositoryPort;
import br.com.recargapay.wallet_service.application.service.WalletApplicationService;
import br.com.recargapay.wallet_service.domain.exception.WalletNotFoundException;
import br.com.recargapay.wallet_service.domain.model.Wallet;
import br.com.recargapay.wallet_service.domain.model.WalletBalanceChanged;
import br.com.recargapay.wallet_service.domain.service.WalletDomainService;
import br.com.recargapay.wallet_service.infrastructure.adapter.in.web.dto.WalletCreateRequest;
import br.com.recargapay.wallet_service.infrastructure.adapter.in.web.dto.WalletDepositRequest;
import br.com.recargapay.wallet_service.infrastructure.adapter.in.web.dto.WalletResponse;
import br.com.recargapay.wallet_service.infrastructure.adapter.in.web.dto.WalletWithdrawRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class WalletApplicationServiceTest {

    private WalletRepositoryPort repository;
    private WalletApplicationService service;
    private WalletBalanceEventMessagingPort publisher;
    
    @BeforeEach
    void setup() {
        repository = mock(WalletRepositoryPort.class);
        publisher = mock(WalletBalanceEventMessagingPort.class);
        WalletDomainService domainService = new WalletDomainService();
        service = new WalletApplicationService(repository, domainService, publisher);
    }

    @Test
    void createWalletShouldReturnWalletResponse() {
        Wallet wallet = new Wallet(1L, BigDecimal.valueOf(100));

        when(repository.save(any(Wallet.class))).thenReturn(wallet);

        WalletCreateRequest request = new WalletCreateRequest();
        request.setInitialAmount(wallet.getBalance());
        WalletResponse response = service.createWallet(request);

        assertEquals(wallet.getId(), response.walletId());
        assertEquals(wallet.getBalance(), response.balance());
        verify(repository, times(1)).save(any(Wallet.class));
        verify(publisher, times(1)).publish(eq(wallet.getId()), any(WalletBalanceChanged.class));
    }

    @Test
    void depositShouldUpdateWalletBalance() {
        Wallet wallet = new Wallet(1L, BigDecimal.valueOf(100));
        wallet.clearEvents();
        when(repository.findById(wallet.getId())).thenReturn(Optional.of(wallet));

        WalletDepositRequest request = new WalletDepositRequest();
        request.setAmount(BigDecimal.valueOf(50));
        service.deposit(wallet.getId(), request);

        assertEquals(BigDecimal.valueOf(150), wallet.getBalance());
        verify(repository, times(1)).save(wallet);
        verify(publisher, times(1)).publish(eq(wallet.getId()), any(WalletBalanceChanged.class));
    }

    @Test
    void withdrawShouldUpdateWalletBalance() {
        Wallet wallet = new Wallet(1L, BigDecimal.valueOf(100));
        wallet.clearEvents();
        when(repository.findById(wallet.getId())).thenReturn(Optional.of(wallet));

        WalletWithdrawRequest request = new WalletWithdrawRequest();
        request.setAmount(BigDecimal.valueOf(30));
        service.withdraw(wallet.getId(), request);

        assertEquals(BigDecimal.valueOf(70), wallet.getBalance());
        verify(repository, times(1)).save(wallet);
        verify(publisher, times(1)).publish(eq(wallet.getId()), any(WalletBalanceChanged.class));
    }

    @Test
    void getBalanceShouldReturnCorrectBalance() {
        Wallet wallet = new Wallet(1L, BigDecimal.TEN);
        when(repository.findById(wallet.getId())).thenReturn(Optional.of(wallet));

        assertEquals(service.getBalance(wallet.getId()), wallet.getBalance());
        verify(repository, times(1)).findById(wallet.getId());
    }

    @Test
    void getBalanceShouldNotFindWallet() {
        assertThrows(WalletNotFoundException.class, () -> service.getBalance(1L));
    }
}
