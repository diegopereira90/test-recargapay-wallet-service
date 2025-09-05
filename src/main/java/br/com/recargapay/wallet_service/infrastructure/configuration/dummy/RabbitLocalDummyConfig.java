package br.com.recargapay.wallet_service.infrastructure.configuration.dummy;

import br.com.recargapay.wallet_service.application.port.out.WalletBalanceEventMessagingPort;
import br.com.recargapay.wallet_service.application.port.out.WalletBalanceHistoryRepositoryPort;
import br.com.recargapay.wallet_service.domain.model.WalletBalanceChanged;
import br.com.recargapay.wallet_service.infrastructure.adapter.in.messaging.WalletBalanceHistoryConsumer;
import br.com.recargapay.wallet_service.infrastructure.configuration.RabbitConfig;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.Connection;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("local")
public class RabbitLocalDummyConfig {

    @Bean
    public TopicExchange walletExchange() {
        return new TopicExchange(RabbitConfig.EXCHANGE);
    }

    @Bean
    public Queue walletHistoryQueue() {
        return new Queue(RabbitConfig.QUEUE, true);
    }

    @Bean
    public Binding walletBinding(Queue walletHistoryQueue, TopicExchange walletExchange) {
        return BindingBuilder.bind(walletHistoryQueue).to(walletExchange).with(RabbitConfig.ROUTING_KEY);
    }

    @Bean
    public RabbitTemplate rabbitTemplate() {
        return new RabbitTemplate(fakeConnectionFactory()) {
            @Override
            public void convertAndSend(String exchange, String routingKey, Object message) {
                System.out.printf(
                    "[LOCAL DUMMY RabbitTemplate] convertAndSend called with exchange=%s, routingKey=%s, message=%s%n",
                    exchange, routingKey, message);
            }
        };
    }

    @Bean
    public WalletBalanceEventMessagingPort walletBalancePublisher() {
        return new WalletBalanceEventMessagingPort() {
            @Override
            public void publish(Long walletId, WalletBalanceChanged event) {
                System.out.printf("[DUMMY PUBLISHER] walletId=%s, balance=%s%n", walletId, event.getBalance());
            }
        };
    }

    @Bean
    public WalletBalanceHistoryConsumer walletBalanceListener(WalletBalanceHistoryRepositoryPort repository) {
        return new WalletBalanceHistoryConsumer(repository) {
        };
    }

    private ConnectionFactory fakeConnectionFactory() {
        return new ConnectionFactory() {
            @Override
            public Connection createConnection() {
                throw new UnsupportedOperationException("Dummy RabbitTemplate: createConnection não deve ser chamado");
            }

            @Override
            public String getHost() {
                return "";
            }

            @Override
            public int getPort() {
                return 0;
            }

            @Override
            public String getVirtualHost() {
                return "";
            }

            @Override
            public String getUsername() {
                return "";
            }

            @Override
            public void addConnectionListener(ConnectionListener listener) {
            }

            @Override
            public boolean removeConnectionListener(ConnectionListener listener) {
                return false;
            }

            @Override
            public void clearConnectionListeners() {
            }
        };
    }
}
