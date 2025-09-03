package br.com.recargapay.wallet_service.infrastructure.adapter.out.messaging;

import br.com.recargapay.wallet_service.application.port.out.WalletBalanceEventMessagingPort;
import br.com.recargapay.wallet_service.domain.model.WalletBalanceChanged;
import br.com.recargapay.wallet_service.infrastructure.adapter.out.messaging.event.WalletBalanceUpdatedEvent;
import br.com.recargapay.wallet_service.infrastructure.configuration.RabbitConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class RabbitWalletBalanceHistoryEventPublisher implements WalletBalanceEventMessagingPort {

    private static final Logger log = LoggerFactory.getLogger(RabbitWalletBalanceHistoryEventPublisher.class);
    private final RabbitTemplate rabbitTemplate;

    public RabbitWalletBalanceHistoryEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public void publish(Long walletId, WalletBalanceChanged event) {
        log.info("Publish event WalletBalanceHistoryEvent for walletId={} with balance={} in date={}", walletId,
            event.getBalance(), event.getUpdatedAt());

        rabbitTemplate.convertAndSend(RabbitConfig.EXCHANGE, RabbitConfig.ROUTING_KEY,
            new WalletBalanceUpdatedEvent(walletId, event.getBalance(), event.getUpdatedAt()));

        log.debug("Wallet balance history event published with success for walletId={}", walletId);
    }
}
