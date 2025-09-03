package br.com.recargapay.wallet_service.application.service;

import br.com.recargapay.wallet_service.application.port.in.WalletUseCase;
import br.com.recargapay.wallet_service.application.port.out.WalletBalanceEventMessagingPort;
import br.com.recargapay.wallet_service.application.port.out.WalletRepositoryPort;
import br.com.recargapay.wallet_service.domain.exception.WalletNotFoundException;
import br.com.recargapay.wallet_service.domain.model.Wallet;
import br.com.recargapay.wallet_service.domain.service.WalletDomainService;
import br.com.recargapay.wallet_service.infrastructure.adapter.in.web.dto.WalletCreateRequest;
import br.com.recargapay.wallet_service.infrastructure.adapter.in.web.dto.WalletDepositRequest;
import br.com.recargapay.wallet_service.infrastructure.adapter.in.web.dto.WalletResponse;
import br.com.recargapay.wallet_service.infrastructure.adapter.in.web.dto.WalletTransferRequest;
import br.com.recargapay.wallet_service.infrastructure.adapter.in.web.dto.WalletWithdrawRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class WalletApplicationService implements WalletUseCase {

    private static final Logger log = LoggerFactory.getLogger(WalletApplicationService.class);
    private final WalletRepositoryPort repository;
    private final WalletDomainService domainService;
    private final WalletBalanceEventMessagingPort publisher;

    public WalletApplicationService(
        WalletRepositoryPort repository,
        WalletDomainService domainService,
        WalletBalanceEventMessagingPort publisher) {
        this.repository = repository;
        this.domainService = domainService;
        this.publisher = publisher;
    }

    @Override
    public WalletResponse createWallet(WalletCreateRequest request) {
        log.info("Create wallet requested: amount={}", request.getInitialAmount());

        Wallet wallet = new Wallet(null, request.getInitialAmount());
        Wallet saved = repository.save(wallet);
        wallet.setId(saved.getId());

        publishWalletBalanceChanged(wallet);
        log.debug("Wallet created successfully with walletId={}", saved.getId());
        return new WalletResponse(saved.getId(), saved.getBalance());
    }

    @Override
    public void deposit(Long walletId, WalletDepositRequest request) {
        log.info("Wallet deposit requested: walletId={}, amount={}", walletId, request.getAmount());

        Wallet wallet =
            repository.findById(walletId)
                .orElseThrow(() -> new WalletNotFoundException("Wallet not found when try to make a deposit"));
        wallet.deposit(request.getAmount());
        repository.save(wallet);

        publishWalletBalanceChanged(wallet);
        log.debug("Wallet deposit processed successfully for walletId={}", walletId);
    }

    @Override
    public void withdraw(Long walletId, WalletWithdrawRequest request) {
        log.info("Wallet withdraw requested: walletId={}, amount={}", walletId, request.getAmount());

        Wallet wallet =
            repository.findById(walletId)
                .orElseThrow(() -> new WalletNotFoundException("Wallet not found when try to make a withdraw"));
        wallet.withdraw(request.getAmount());
        repository.save(wallet);

        publishWalletBalanceChanged(wallet);
        log.debug("Wallet withdraw processed successfully for walletId={}", walletId);
    }

    @Override
    public void transfer(Long walletId, WalletTransferRequest request) {
        log.info("Wallet transfer requested: fromWalletId={}, toWalletId={}, amount={}", walletId,
            request.getDestinationWalletId(), request.getAmount());

        Wallet from = repository.findById(walletId)
            .orElseThrow(() -> new WalletNotFoundException("Wallet origin not found when try to make a transfer"));
        Wallet to = repository.findById(request.getDestinationWalletId())
            .orElseThrow(() -> new WalletNotFoundException("Wallet destination not found when try to make a transfer"));
        domainService.transfer(from, to, request.getAmount());
        repository.save(from);
        repository.save(to);

        publishWalletBalanceChanged(from);
        publishWalletBalanceChanged(to);
        log.debug("Wallet transfer processed successfully from walletId={}", walletId);
    }

    @Override
    public BigDecimal getBalance(Long walletId) {
        log.info("Wallet balance requested: walletId={}", walletId);

        Wallet wallet =
            repository.findById(walletId)
                .orElseThrow(() -> new WalletNotFoundException("Wallet not found when try to check the balance"));

        log.debug("Wallet balance checked successfully from walletId={}", walletId);
        return wallet.getBalance();
    }

    private void publishWalletBalanceChanged(Wallet wallet) {
        wallet.getEvents().forEach(e -> publisher.publish(wallet.getId(), e));
        wallet.clearEvents();
    }
}