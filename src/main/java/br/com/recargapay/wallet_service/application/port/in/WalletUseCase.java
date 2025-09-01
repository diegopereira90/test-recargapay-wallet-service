package br.com.recargapay.wallet_service.application.port.in;

import br.com.recargapay.wallet_service.infrastructure.adapter.in.web.dto.WalletBalanceHistoryRequest;
import br.com.recargapay.wallet_service.infrastructure.adapter.in.web.dto.WalletCreateRequest;
import br.com.recargapay.wallet_service.infrastructure.adapter.in.web.dto.WalletDepositRequest;
import br.com.recargapay.wallet_service.infrastructure.adapter.in.web.dto.WalletResponse;
import br.com.recargapay.wallet_service.infrastructure.adapter.in.web.dto.WalletTransferRequest;
import br.com.recargapay.wallet_service.infrastructure.adapter.in.web.dto.WalletWithdrawRequest;

import java.math.BigDecimal;

public interface WalletUseCase {

    WalletResponse createWallet(WalletCreateRequest request);

    void deposit(Long walletId, WalletDepositRequest request);

    void withdraw(Long walletId, WalletWithdrawRequest request);

    void transfer(Long walletId, WalletTransferRequest request);

    BigDecimal getBalance(Long walletId);

    BigDecimal getBalanceHistory(Long walletId, WalletBalanceHistoryRequest request);
}
