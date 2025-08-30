package br.com.recargapay.wallet_service.domain.service;

import br.com.recargapay.wallet_service.domain.model.Wallet;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class WalletDomainService {

    public void transfer(Wallet fromWallet, Wallet toWallet, BigDecimal amount) {
        fromWallet.withdraw(amount);
        toWallet.deposit(amount);
    }
}
