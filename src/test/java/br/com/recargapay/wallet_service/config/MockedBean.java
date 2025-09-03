package br.com.recargapay.wallet_service.config;

import br.com.recargapay.wallet_service.application.port.out.WalletBalanceEventMessagingPort;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class MockedBean {

    @Bean
    public WalletBalanceEventMessagingPort publisher() {
        return Mockito.mock(WalletBalanceEventMessagingPort.class);
    }

}
