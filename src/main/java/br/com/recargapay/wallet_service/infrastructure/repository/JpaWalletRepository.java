package br.com.recargapay.wallet_service.infrastructure.repository;

import br.com.recargapay.wallet_service.infrastructure.entity.WalletEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaWalletRepository extends JpaRepository<WalletEntity, Long> {}
