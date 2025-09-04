package br.com.recargapay.wallet_service.config;

import br.com.recargapay.wallet_service.application.port.out.WalletBalanceEventMessagingPort;
import br.com.recargapay.wallet_service.infrastructure.configuration.JwtRequestFilter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;
import java.util.List;

@TestConfiguration
public class MockedBean {

    @Bean
    public WalletBalanceEventMessagingPort publisher() {
        return Mockito.mock(WalletBalanceEventMessagingPort.class);
    }

    @Bean
    public JwtRequestFilter jwtRequestFilter() {
        return new JwtRequestFilter(null) {
            @Override
            protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
                throws ServletException, IOException {
                SecurityContextHolder.getContext().setAuthentication(
                    new UsernamePasswordAuthenticationToken("testuser", null,
                        List.of(new SimpleGrantedAuthority("ROLE_USER")))
                );
                chain.doFilter(request, response);
            }
        };
    }

}
