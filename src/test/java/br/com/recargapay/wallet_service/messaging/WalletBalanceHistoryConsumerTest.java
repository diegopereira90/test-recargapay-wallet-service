package br.com.recargapay.wallet_service.messaging;

import br.com.recargapay.wallet_service.application.port.out.WalletBalanceHistoryRepositoryPort;
import br.com.recargapay.wallet_service.infrastructure.adapter.in.messaging.WalletBalanceHistoryConsumer;
import br.com.recargapay.wallet_service.infrastructure.adapter.out.messaging.event.WalletBalanceUpdatedEvent;
import br.com.recargapay.wallet_service.infrastructure.adapter.out.persistence.JpaWalletBalanceHistoryRepository;
import br.com.recargapay.wallet_service.infrastructure.adapter.out.persistence.WalletBalanceHistoryRepositoryAdapter;
import br.com.recargapay.wallet_service.infrastructure.adapter.out.persistence.entity.WalletBalanceHistoryEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class WalletBalanceHistoryConsumerTest {

    private WalletBalanceHistoryConsumer consumer;
    private WalletBalanceHistoryRepositoryPort repository;
    private JpaWalletBalanceHistoryRepository jpaRepository;

    @BeforeEach
    void setup() {
        jpaRepository = mock(JpaWalletBalanceHistoryRepository.class);
        repository = new WalletBalanceHistoryRepositoryAdapter(jpaRepository);
        consumer = new WalletBalanceHistoryConsumer(repository);
    }

    @Test
    void consumeShouldSaveWalletBalanceHistoryEvent() {
        WalletBalanceUpdatedEvent event = new WalletBalanceUpdatedEvent();
        event.setWalletId(1L);
        event.setBalance(BigDecimal.valueOf(100));
        event.setUpdatedAt(LocalDateTime.now());

        WalletBalanceHistoryEntity savedEntity = new WalletBalanceHistoryEntity();
        savedEntity.setWalletId(event.getWalletId());
        savedEntity.setBalance(event.getBalance());
        savedEntity.setUpdatedAt(event.getUpdatedAt());
        savedEntity.setId(10L);

        when(jpaRepository.save(any(WalletBalanceHistoryEntity.class))).thenReturn(savedEntity);
        consumer.consume(event);

        ArgumentCaptor<WalletBalanceHistoryEntity> captor = ArgumentCaptor.forClass(WalletBalanceHistoryEntity.class);
        verify(jpaRepository, times(1)).save(captor.capture());
        WalletBalanceHistoryEntity captured = captor.getValue();

        assertThat(captured.getWalletId()).isEqualTo(event.getWalletId());
        assertThat(captured.getBalance()).isEqualByComparingTo(event.getBalance());
        assertThat(captured.getUpdatedAt()).isEqualTo(event.getUpdatedAt());
    }
}