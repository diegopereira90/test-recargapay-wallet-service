package br.com.recargapay.wallet_service.application.service;

import br.com.recargapay.wallet_service.application.dto.HistoricalBalanceRequest;
import br.com.recargapay.wallet_service.application.dto.TransferRequest;
import br.com.recargapay.wallet_service.application.dto.WalletCreateRequest;
import br.com.recargapay.wallet_service.application.dto.WalletResponse;
import br.com.recargapay.wallet_service.domain.model.Wallet;
import br.com.recargapay.wallet_service.domain.service.WalletDomainService;
import br.com.recargapay.wallet_service.ports.in.WalletUseCase;
import br.com.recargapay.wallet_service.ports.out.WalletRepositoryPort;
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
        Wallet wallet = new Wallet(null, request.initialBalance());
        Wallet saved = repository.save(wallet);
        return new WalletResponse(saved.getId(), saved.getBalance());
    }

    @Override
    public void deposit(Long walletId, BigDecimal amount) {
        Wallet wallet = repository.findById(walletId).orElseThrow();
        wallet.deposit(amount);
        repository.save(wallet);
    }

    @Override
    public void withdraw(Long walletId, BigDecimal amount) {
        Wallet wallet = repository.findById(walletId).orElseThrow();
        wallet.withdraw(amount);
        repository.save(wallet);
    }

    @Override
    public void transfer(TransferRequest request) {
        Wallet from = repository.findById(request.fromWalletId()).orElseThrow();
        Wallet to = repository.findById(request.toWalletId()).orElseThrow();
        domainService.transfer(from, to, request.amount());
        repository.save(from);
        repository.save(to);
    }

    @Override
    public BigDecimal getBalance(Long walletId) {
        Wallet wallet = repository.findById(walletId).orElseThrow();
        return wallet.getBalance();
    }

    @Override
    public BigDecimal getHistoricalBalance(HistoricalBalanceRequest request) {
        Wallet wallet = repository.findById(request.walletId()).orElseThrow();
        return wallet.getBalanceAt(request.updatedAt());
    }
}