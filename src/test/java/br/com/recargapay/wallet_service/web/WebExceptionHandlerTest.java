package br.com.recargapay.wallet_service.web;

import br.com.recargapay.wallet_service.config.MockedBean;
import br.com.recargapay.wallet_service.domain.model.Wallet;
import br.com.recargapay.wallet_service.infrastructure.adapter.in.web.dto.WalletDepositRequest;
import br.com.recargapay.wallet_service.infrastructure.adapter.in.web.dto.WalletTransferRequest;
import br.com.recargapay.wallet_service.infrastructure.adapter.in.web.handler.WebErrorResponse;
import br.com.recargapay.wallet_service.infrastructure.adapter.in.web.handler.WebExceptionHandler;
import br.com.recargapay.wallet_service.infrastructure.adapter.out.persistence.JpaWalletRepository;
import br.com.recargapay.wallet_service.infrastructure.adapter.out.persistence.entity.WalletEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithMockUser;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(MockedBean.class)
@WithMockUser(username = "testuser", roles = {"USER"})
class WebExceptionHandlerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private JpaWalletRepository walletRepository;

    @BeforeEach
    void setup() {
        walletRepository.deleteAll();
    }

    @Test
    void getBalance_nonExistentWallet_shouldReturn404() {
        ResponseEntity<WebErrorResponse> response = restTemplate.getForEntity(
            "http://localhost:" + port + "/api/wallets/1/balance",
            WebErrorResponse.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getMessage()).isEqualTo("Wallet not found when try to check the balance");
    }

    @Test
    void withdraw_insufficientBalance_shouldReturn400() {
        WalletEntity source = walletRepository.saveAndFlush(new WalletEntity(new Wallet(null, BigDecimal.valueOf(10))));

        WalletDepositRequest request = new WalletDepositRequest();
        request.setAmount(BigDecimal.valueOf(100));

        ResponseEntity<WebErrorResponse> response = restTemplate.postForEntity(
            "http://localhost:" + port + "/api/wallets/" + source.getId() + "/withdraw",
            request,
            WebErrorResponse.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().getMessage()).contains("Insufficient balance for withdraw/transfer");
    }

    @Test
    void transfer_invalid_shouldReturn400() {
        walletRepository.saveAndFlush(new WalletEntity(new Wallet(null, BigDecimal.valueOf(10))));
        Long walletId = walletRepository.findAll().getFirst().getId();

        WalletTransferRequest request = new WalletTransferRequest();
        request.setAmount(BigDecimal.valueOf(100));
        request.setDestinationWalletId(walletId);

        ResponseEntity<WebErrorResponse> response = restTemplate.postForEntity(
            "http://localhost:" + port + "/api/wallets/" + walletId + "/transfer",
            request,
            WebErrorResponse.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().getMessage()).contains("Cannot transfer to the same wallet");
    }

    @Test
    void handleGenericException_shouldReturn500() {
        Exception ex = new Exception("Unexpected error");
        WebExceptionHandler handler = new WebExceptionHandler();

        ResponseEntity<WebErrorResponse> response = handler.handleGeneric(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getMessage()).isEqualTo("Unexpected error occurred");
    }
}

