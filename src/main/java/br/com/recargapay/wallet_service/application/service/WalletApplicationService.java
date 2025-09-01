package br.com.recargapay.wallet_service.application.service;

import br.com.recargapay.wallet_service.application.port.in.WalletUseCase;
import br.com.recargapay.wallet_service.application.port.out.WalletRepositoryPort;
import br.com.recargapay.wallet_service.domain.exception.WalletNotFoundException;
import br.com.recargapay.wallet_service.domain.model.Wallet;
import br.com.recargapay.wallet_service.domain.service.WalletDomainService;
import br.com.recargapay.wallet_service.infrastructure.adapter.in.web.dto.WalletBalanceHistoryRequest;
import br.com.recargapay.wallet_service.infrastructure.adapter.in.web.dto.WalletCreateRequest;
import br.com.recargapay.wallet_service.infrastructure.adapter.in.web.dto.WalletDepositRequest;
import br.com.recargapay.wallet_service.infrastructure.adapter.in.web.dto.WalletResponse;
import br.com.recargapay.wallet_service.infrastructure.adapter.in.web.dto.WalletTransferRequest;
import br.com.recargapay.wallet_service.infrastructure.adapter.in.web.dto.WalletWithdrawRequest;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class WalletApplicationService implements WalletUseCase {

    private final WalletRepositoryPort repository;
    private final WalletDomainService domainService;

    public WalletApplicationService(
        WalletRepositoryPort repository,
        WalletDomainService domainService) {
        this.repository = repository;
        this.domainService = domainService;
    }

    @Override
    public WalletResponse createWallet(WalletCreateRequest request) {
        Wallet wallet = new Wallet(null, request.getInitialAmount());
        Wallet saved = repository.save(wallet);
        return new WalletResponse(saved.getId(), saved.getBalance());
    }

    @Override
    public void deposit(Long walletId, WalletDepositRequest request) {
        Wallet wallet =
            repository.findById(walletId).orElseThrow(() -> new WalletNotFoundException("Wallet not found"));
        wallet.deposit(request.getAmount());
        repository.save(wallet);
    }

    @Override
    public void withdraw(Long walletId, WalletWithdrawRequest request) {
        Wallet wallet =
            repository.findById(walletId).orElseThrow(() -> new WalletNotFoundException("Wallet not found"));
        wallet.withdraw(request.getAmount());
        repository.save(wallet);
    }

    @Override
    public void transfer(Long walletId, WalletTransferRequest request) {
        Wallet from = repository.findById(walletId).orElseThrow(() -> new WalletNotFoundException("Wallet not found"));
        Wallet to = repository.findById(request.getDestinationWalletId())
            .orElseThrow(() -> new WalletNotFoundException("Wallet not found"));
        domainService.transfer(from, to, request.getAmount());
        repository.save(from);
        repository.save(to);
    }

    @Override
    public BigDecimal getBalance(Long walletId) {
        Wallet wallet =
            repository.findById(walletId).orElseThrow(() -> new WalletNotFoundException("Wallet not found"));
        return wallet.getBalance();
    }

    @Override
    public BigDecimal getBalanceHistory(Long walletId, WalletBalanceHistoryRequest request) {
        Wallet wallet =
            repository.findById(walletId).orElseThrow(() -> new WalletNotFoundException("Wallet not found"));
        return wallet.getBalanceAt(request.getBalanceAt());
    }
}