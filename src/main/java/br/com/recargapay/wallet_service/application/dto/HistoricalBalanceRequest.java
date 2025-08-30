package br.com.recargapay.wallet_service.application.dto;

import java.time.LocalDateTime;

public record HistoricalBalanceRequest(Long walletId, LocalDateTime updatedAt) {
}
