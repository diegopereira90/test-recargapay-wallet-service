package br.com.recargapay.wallet_service.application.port.out;

import br.com.recargapay.wallet_service.infrastructure.adapter.out.messaging.event.WalletBalanceUpdatedEvent;
import br.com.recargapay.wallet_service.infrastructure.adapter.out.persistence.entity.WalletBalanceHistoryEntity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public interface WalletBalanceHistoryRepositoryPort {

    WalletBalanceHistoryEntity save(WalletBalanceUpdatedEvent event);

    BigDecimal balanceAt(Long walletId, LocalDateTime date);
}