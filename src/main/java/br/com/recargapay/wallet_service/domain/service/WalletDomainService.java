package br.com.recargapay.wallet_service.domain.service;

import br.com.recargapay.wallet_service.domain.exception.InvalidTransferException;
import br.com.recargapay.wallet_service.domain.model.Wallet;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class WalletDomainService {

    public void transfer(Wallet fromWallet, Wallet toWallet, BigDecimal amount) {
        if (fromWallet.getId().equals(toWallet.getId())) {
            throw new InvalidTransferException("Cannot transfer to the same wallet");
        }
        fromWallet.withdraw(amount);
        toWallet.deposit(amount);
    }
}
