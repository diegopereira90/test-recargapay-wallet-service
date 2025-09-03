package br.com.recargapay.wallet_service.application.port.out;

import br.com.recargapay.wallet_service.domain.model.WalletBalanceChanged;

public interface WalletBalanceEventMessagingPort {
    void publish(Long walletId, WalletBalanceChanged event);
}
