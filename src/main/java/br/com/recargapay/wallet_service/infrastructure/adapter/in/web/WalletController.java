package br.com.recargapay.wallet_service.infrastructure.adapter.in.web;

import br.com.recargapay.wallet_service.application.port.in.WalletUseCase;
import br.com.recargapay.wallet_service.infrastructure.adapter.in.web.dto.WalletBalanceHistoryRequest;
import br.com.recargapay.wallet_service.infrastructure.adapter.in.web.dto.WalletCreateRequest;
import br.com.recargapay.wallet_service.infrastructure.adapter.in.web.dto.WalletDepositRequest;
import br.com.recargapay.wallet_service.infrastructure.adapter.in.web.dto.WalletResponse;
import br.com.recargapay.wallet_service.infrastructure.adapter.in.web.dto.WalletTransferRequest;
import br.com.recargapay.wallet_service.infrastructure.adapter.in.web.dto.WalletWithdrawRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/wallets")
public class WalletController {

    private final WalletUseCase walletService;

    public WalletController(WalletUseCase walletService) {
        this.walletService = walletService;
    }

    @PostMapping
    public ResponseEntity<WalletResponse> createWallet(@RequestBody WalletCreateRequest request) {
        WalletResponse response = walletService.createWallet(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{walletId}/deposit")
    public ResponseEntity<Void> deposit(
        @PathVariable Long walletId,
        @Valid @RequestBody WalletDepositRequest request) {
        walletService.deposit(walletId, request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{walletId}/withdraw")
    public ResponseEntity<Void> withdraw(
        @PathVariable Long walletId,
        @Valid @RequestBody WalletWithdrawRequest request) {
        walletService.withdraw(walletId, request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("{walletId}/transfer")
    public ResponseEntity<Void> transfer(
        @PathVariable Long walletId,
        @Valid @RequestBody WalletTransferRequest request) {
        walletService.transfer(walletId, request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{walletId}/balance")
    public ResponseEntity<BigDecimal> getBalance(@PathVariable Long walletId) {
        BigDecimal balance = walletService.getBalance(walletId);
        return ResponseEntity.ok(balance);
    }

    @PostMapping("/{walletId}/balance/history")
    public ResponseEntity<BigDecimal> getBalanceHistory(
        @PathVariable Long walletId,
        @Valid @RequestBody WalletBalanceHistoryRequest request) {
        BigDecimal balance = walletService.getBalanceHistory(walletId, request);
        return ResponseEntity.ok(balance);
    }
}