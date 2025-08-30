package br.com.recargapay.wallet_service.ports.out;

import br.com.recargapay.wallet_service.domain.model.WalletBalanceHistory;

import java.util.List;

public interface WalletBalanceHistoryRepositoryPort {
    WalletBalanceHistory save(WalletBalanceHistory history);
    List<WalletBalanceHistory> findByWalletId(Long walletId);
}