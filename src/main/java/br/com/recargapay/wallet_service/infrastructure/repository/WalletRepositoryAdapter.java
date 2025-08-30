package br.com.recargapay.wallet_service.infrastructure.repository;

import br.com.recargapay.wallet_service.domain.model.Wallet;
import br.com.recargapay.wallet_service.infrastructure.entity.WalletEntity;
import br.com.recargapay.wallet_service.ports.out.WalletRepositoryPort;
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
        WalletEntity saved = repository.save(entity);
        return entity.toDomain();
    }

    @Override
    public Optional<Wallet> findById(Long id) {
        return repository.findById(id).map(WalletEntity::toDomain);
    }
}
