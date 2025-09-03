package br.com.recargapay.wallet_service.infrastructure.adapter.out.persistence.entity;

import br.com.recargapay.wallet_service.domain.model.Wallet;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;

@Entity
@Table(name = "wallets")
public class WalletEntity extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private BigDecimal balance;

    public WalletEntity() {
    }

    public WalletEntity(Wallet wallet) {
        this.id = wallet.getId();
        this.balance = wallet.getBalance();
    }

    public Long getId() {
        return id;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public Wallet toDomain() {
        return new Wallet(this.id, this.balance, false);
    }
}
