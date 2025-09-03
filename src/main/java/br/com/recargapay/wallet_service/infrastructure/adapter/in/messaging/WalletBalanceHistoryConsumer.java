package br.com.recargapay.wallet_service.infrastructure.adapter.in.messaging;

import br.com.recargapay.wallet_service.application.port.out.WalletBalanceHistoryRepositoryPort;
import br.com.recargapay.wallet_service.infrastructure.adapter.out.messaging.event.WalletBalanceUpdatedEvent;
import br.com.recargapay.wallet_service.infrastructure.adapter.out.persistence.entity.WalletBalanceHistoryEntity;
import br.com.recargapay.wallet_service.infrastructure.configuration.RabbitConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class WalletBalanceHistoryConsumer {

    private static final Logger log = LoggerFactory.getLogger(WalletBalanceHistoryConsumer.class);
    private final WalletBalanceHistoryRepositoryPort repository;

    public WalletBalanceHistoryConsumer(WalletBalanceHistoryRepositoryPort repository) {
        this.repository = repository;
    }

    @RabbitListener(queues = RabbitConfig.QUEUE)
    @Transactional
    public void consume(WalletBalanceUpdatedEvent event) {
        log.info("Wallet balance history event consumed with success for walletId={} with balance={} in date={}",
            event.getWalletId(), event.getBalance(), event.getUpdatedAt());
        WalletBalanceHistoryEntity entity = repository.save(event);
        log.debug("Wallet balance history event saved with success for walletId={} with id={}", entity.getWalletId(),
            entity.getId());
    }
}
