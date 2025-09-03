package br.com.recargapay.wallet_service.messaging;

import br.com.recargapay.wallet_service.domain.model.WalletBalanceChanged;
import br.com.recargapay.wallet_service.infrastructure.adapter.out.messaging.RabbitWalletBalanceHistoryEventPublisher;
import br.com.recargapay.wallet_service.infrastructure.adapter.out.messaging.event.WalletBalanceUpdatedEvent;
import br.com.recargapay.wallet_service.infrastructure.configuration.RabbitConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class RabbitWalletBalanceHistoryEventPublisherTest {

    private RabbitTemplate rabbitTemplate;
    private RabbitWalletBalanceHistoryEventPublisher publisher;

    @BeforeEach
    void setup() {
        rabbitTemplate = mock(RabbitTemplate.class);
        publisher = new RabbitWalletBalanceHistoryEventPublisher(rabbitTemplate);
    }

    @Test
    void publishShouldSendEventToRabbit() {
        Long walletId = 1L;
        BigDecimal balance = BigDecimal.valueOf(100);
        LocalDateTime updatedAt = LocalDateTime.now();
        WalletBalanceChanged event = new WalletBalanceChanged(balance, updatedAt);

        publisher.publish(walletId, event);
        ArgumentCaptor<WalletBalanceUpdatedEvent> captor = ArgumentCaptor.forClass(WalletBalanceUpdatedEvent.class);
        verify(rabbitTemplate, times(1))
            .convertAndSend(eq(RabbitConfig.EXCHANGE), eq(RabbitConfig.ROUTING_KEY), captor.capture());

        WalletBalanceUpdatedEvent sentEvent = captor.getValue();
        assertThat(sentEvent.getWalletId()).isEqualTo(walletId);
        assertThat(sentEvent.getBalance()).isEqualByComparingTo(balance);
        assertThat(sentEvent.getUpdatedAt()).isEqualTo(updatedAt);
    }
}
