package br.com.recargapay.wallet_service.ports.in;

import br.com.recargapay.wallet_service.application.dto.HistoricalBalanceRequest;
import br.com.recargapay.wallet_service.application.dto.TransferRequest;
import br.com.recargapay.wallet_service.application.dto.WalletCreateRequest;
import br.com.recargapay.wallet_service.application.dto.WalletResponse;

import java.math.BigDecimal;

public interface WalletUseCase {

    WalletResponse createWallet(WalletCreateRequest request);

    void deposit(Long walletId, BigDecimal amount);

    void withdraw(Long walletId, BigDecimal amount);

    void transfer(TransferRequest request);

    BigDecimal getBalance(Long walletId);

    BigDecimal getHistoricalBalance(HistoricalBalanceRequest request);
}
