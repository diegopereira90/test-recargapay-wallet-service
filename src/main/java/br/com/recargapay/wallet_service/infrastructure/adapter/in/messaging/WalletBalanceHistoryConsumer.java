package br.com.recargapay.wallet_service.infrastructure.adapter.in.messaging;

import br.com.recargapay.wallet_service.application.port.out.WalletBalanceHistoryRepositoryPort;
import br.com.recargapay.wallet_service.infrastructure.adapter.out.messaging.event.WalletBalanceUpdatedEvent;
import br.com.recargapay.wallet_service.infrastructure.adapter.out.persistence.entity.WalletBalanceHistoryEntity;
import br.com.recargapay.wallet_service.infrastructure.configuration.RequestIdFilterConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Profile("!local")
public class WalletBalanceHistoryConsumer {

    private static final Logger log = LoggerFactory.getLogger(WalletBalanceHistoryConsumer.class);
    private final WalletBalanceHistoryRepositoryPort repository;

    public WalletBalanceHistoryConsumer(WalletBalanceHistoryRepositoryPort repository) {
        this.repository = repository;
    }

    @Transactional
    @RabbitListener(queues = "#{@walletHistoryQueue.name}")
    public void consume(WalletBalanceUpdatedEvent event) {
        log.info("Wallet balance history event consumed with success for walletId={} with balance={} in date={}",
            event.getWalletId(), event.getBalance(), event.getUpdatedAt());
        MDC.put(RequestIdFilterConfig.REQUEST_ID_HEADER, event.getRequestId());
        WalletBalanceHistoryEntity entity = repository.save(event);
        log.debug("Wallet balance history event saved with success for walletId={} with id={}", entity.getWalletId(),
            entity.getId());
    }
}
