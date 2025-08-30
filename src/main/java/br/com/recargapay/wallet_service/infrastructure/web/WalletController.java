package br.com.recargapay.wallet_service.infrastructure.web;

import br.com.recargapay.wallet_service.application.dto.HistoricalBalanceRequest;
import br.com.recargapay.wallet_service.application.dto.TransferRequest;
import br.com.recargapay.wallet_service.application.dto.WalletCreateRequest;
import br.com.recargapay.wallet_service.application.dto.WalletResponse;
import br.com.recargapay.wallet_service.ports.in.WalletUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDateTime;

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
    public ResponseEntity<Void> deposit(@PathVariable Long walletId, @RequestParam BigDecimal amount) {
        walletService.deposit(walletId, amount);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{walletId}/withdraw")
    public ResponseEntity<Void> withdraw(@PathVariable Long walletId, @RequestParam BigDecimal amount) {
        walletService.withdraw(walletId, amount);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/transfer")
    public ResponseEntity<Void> transfer(@RequestBody TransferRequest request) {
        walletService.transfer(request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{walletId}/balance")
    public ResponseEntity<BigDecimal> getBalance(@PathVariable Long walletId) {
        BigDecimal balance = walletService.getBalance(walletId);
        return ResponseEntity.ok(balance);
    }

    @GetMapping("/{walletId}/balance/historical")
    public ResponseEntity<BigDecimal> getHistoricalBalance(
            @PathVariable Long walletId,
            @RequestParam String at) {
        LocalDateTime time = LocalDateTime.parse(at);
        BigDecimal balance = walletService.getHistoricalBalance(
                new HistoricalBalanceRequest(walletId, time));
        return ResponseEntity.ok(balance);
    }
}