package br.com.recargapay.wallet_service.application.service;

import br.com.recargapay.wallet_service.application.port.in.WalletBalanceHistoryUseCase;
import br.com.recargapay.wallet_service.application.port.out.WalletBalanceHistoryRepositoryPort;
import br.com.recargapay.wallet_service.application.port.out.WalletRepositoryPort;
import br.com.recargapay.wallet_service.domain.exception.WalletNotFoundException;
import br.com.recargapay.wallet_service.domain.model.Wallet;
import br.com.recargapay.wallet_service.infrastructure.adapter.in.web.dto.WalletBalanceHistoryRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class WalletBalanceHistoryApplicationService implements WalletBalanceHistoryUseCase {

    private static final Logger log = LoggerFactory.getLogger(WalletBalanceHistoryApplicationService.class);
    private final WalletBalanceHistoryRepositoryPort repository;
    private final WalletRepositoryPort walletRepository;

    public WalletBalanceHistoryApplicationService(
        WalletBalanceHistoryRepositoryPort repository,
        WalletRepositoryPort walletRepository) {
        this.repository = repository;
        this.walletRepository = walletRepository;
    }

    @Override
    public BigDecimal getBalanceAt(Long walletId, WalletBalanceHistoryRequest request) {
        log.info("Wallet balance history requested: walletId={}", walletId);

        Wallet wallet =
            walletRepository.findById(walletId).orElseThrow(
                () -> new WalletNotFoundException("Wallet not found when try to check the balance history"));

        log.debug("Wallet balance history checked successfully from walletId={}", wallet.getId());
        return repository.balanceAt(wallet.getId(), request.getBalanceAt());
    }

}
