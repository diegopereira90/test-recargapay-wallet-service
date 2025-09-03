package br.com.recargapay.wallet_service.infrastructure.adapter.out.persistence;

import br.com.recargapay.wallet_service.application.port.out.WalletRepositoryPort;
import br.com.recargapay.wallet_service.domain.model.Wallet;
import br.com.recargapay.wallet_service.infrastructure.adapter.out.persistence.entity.WalletEntity;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class WalletRepositoryAdapter implements WalletRepositoryPort {

    private final JpaWalletRepository repository;

    public WalletRepositoryAdapter(JpaWalletRepository repository) {
        this.repository = repository;
    }

    @Override
    public Wallet save(Wallet wallet) {
        WalletEntity entity = new WalletEntity(wallet);
        return repository.save(entity).toDomain();
    }

    @Override
    public Optional<Wallet> findById(Long id) {
        return repository.findById(id).map(WalletEntity::toDomain);
    }
}
