package br.com.recargapay.wallet_service.infrastructure.configuration;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    public static final String EXCHANGE = "wallet.balance.exchange";
    public static final String QUEUE = "wallet.balance";
    public static final String ROUTING_KEY = "wallet.balance.event";

    @Bean
    public TopicExchange walletExchange() {
        return new TopicExchange(EXCHANGE);
    }

    @Bean
    public Queue walletHistoryQueue() {
        return new Queue(QUEUE, true);
    }

    @Bean
    public Binding walletBinding(Queue walletHistoryQueue, TopicExchange walletExchange) {
        return BindingBuilder.bind(walletHistoryQueue).to(walletExchange).with(ROUTING_KEY);
    }
}
