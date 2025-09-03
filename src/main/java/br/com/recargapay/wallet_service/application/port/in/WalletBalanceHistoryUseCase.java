package br.com.recargapay.wallet_service.application.port.in;

import br.com.recargapay.wallet_service.infrastructure.adapter.in.web.dto.WalletBalanceHistoryRequest;

import java.math.BigDecimal;

public interface WalletBalanceHistoryUseCase {

    BigDecimal getBalanceAt(Long walletId, WalletBalanceHistoryRequest request);

}
