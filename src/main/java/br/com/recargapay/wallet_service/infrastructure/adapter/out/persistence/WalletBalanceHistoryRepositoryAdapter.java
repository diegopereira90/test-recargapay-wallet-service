package br.com.recargapay.wallet_service.infrastructure.adapter.out.persistence;

import br.com.recargapay.wallet_service.application.port.out.WalletBalanceHistoryRepositoryPort;
import br.com.recargapay.wallet_service.infrastructure.adapter.out.messaging.event.WalletBalanceUpdatedEvent;
import br.com.recargapay.wallet_service.infrastructure.adapter.out.persistence.entity.WalletBalanceHistoryEntity;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Component
public class WalletBalanceHistoryRepositoryAdapter implements WalletBalanceHistoryRepositoryPort {

    private final JpaWalletBalanceHistoryRepository repository;

    public WalletBalanceHistoryRepositoryAdapter(JpaWalletBalanceHistoryRepository repository) {
        this.repository = repository;
    }

    public WalletBalanceHistoryEntity save(WalletBalanceUpdatedEvent event) {
        WalletBalanceHistoryEntity entity = new WalletBalanceHistoryEntity();
        entity.setWalletId(event.getWalletId());
        entity.setBalance(event.getBalance());
        entity.setUpdatedAt(event.getUpdatedAt());

        return repository.save(entity);
    }

    public BigDecimal balanceAt(Long walletId, LocalDateTime date) {
        return repository
            .findFirstByWalletIdAndUpdatedAtLessThanEqualOrderByUpdatedAtDesc(walletId, date)
            .map(WalletBalanceHistoryEntity::getBalance)
            .orElse(BigDecimal.ZERO);
    }
}
