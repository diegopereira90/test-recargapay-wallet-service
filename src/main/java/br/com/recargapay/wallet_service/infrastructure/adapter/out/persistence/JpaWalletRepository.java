package br.com.recargapay.wallet_service.infrastructure.adapter.out.persistence;

import br.com.recargapay.wallet_service.infrastructure.adapter.out.persistence.entity.WalletEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaWalletRepository extends JpaRepository<WalletEntity, Long> {
}
