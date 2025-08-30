package br.com.recargapay.wallet_service.ports.out;

import br.com.recargapay.wallet_service.domain.model.Wallet;

import java.util.Optional;

public interface WalletRepositoryPort {
    Wallet save(Wallet wallet);
    Optional<Wallet> findById(Long id);
}
