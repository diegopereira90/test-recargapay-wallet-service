package br.com.recargapay.wallet_service.application.dto;

import java.math.BigDecimal;

public record WalletCreateRequest(BigDecimal initialBalance) {}
