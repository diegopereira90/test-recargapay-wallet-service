package br.com.recargapay.wallet_service.infrastructure.adapter.out.persistence;

import br.com.recargapay.wallet_service.infrastructure.adapter.out.persistence.entity.WalletBalanceHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface JpaWalletBalanceHistoryRepository extends JpaRepository<WalletBalanceHistoryEntity, Long> {

    Optional<WalletBalanceHistoryEntity> findFirstByWalletIdAndUpdatedAtLessThanEqualOrderByUpdatedAtDesc(
        Long walletId, LocalDateTime date);
}