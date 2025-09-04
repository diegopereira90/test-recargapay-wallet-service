package br.com.recargapay.wallet_service.infrastructure.configuration;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("local")
public class RabbitConfigLocal {

    public static final String EXCHANGE = "wallet.balance.exchange";
    public static final String QUEUE = "wallet.balance.changed";
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

    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter("br.com.recargapay.wallet_service.*");
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory,
                                         Jackson2JsonMessageConverter messageConverter) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter);
        return template;
    }
}

