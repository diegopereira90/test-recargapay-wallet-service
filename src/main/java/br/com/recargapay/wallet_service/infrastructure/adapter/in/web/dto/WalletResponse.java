package br.com.recargapay.wallet_service.infrastructure.adapter.in.web.dto;

import java.math.BigDecimal;

public record WalletResponse(Long walletId, BigDecimal balance) {
}