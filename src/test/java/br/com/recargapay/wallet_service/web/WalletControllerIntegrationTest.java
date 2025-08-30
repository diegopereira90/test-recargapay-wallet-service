package br.com.recargapay.wallet_service.web;

import br.com.recargapay.wallet_service.application.dto.TransferRequest;
import br.com.recargapay.wallet_service.application.dto.WalletCreateRequest;
import br.com.recargapay.wallet_service.application.dto.WalletResponse;
import br.com.recargapay.wallet_service.domain.model.Wallet;
import br.com.recargapay.wallet_service.infrastructure.entity.WalletEntity;
import br.com.recargapay.wallet_service.infrastructure.repository.JpaWalletRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class WalletControllerIntegrationTest {

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
    void createWalletShouldReturnWalletId() {
        WalletCreateRequest request = new WalletCreateRequest(BigDecimal.valueOf(100));

        ResponseEntity<WalletResponse> response = restTemplate.postForEntity(
                "http://localhost:" + port + "/api/wallets",
                request,
                WalletResponse.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().walletId()).isNotNull();
    }

    @Test
    void depositShouldIncreaseWalletBalance() {
        Wallet wallet = new Wallet(null, BigDecimal.valueOf(100));
        WalletEntity entity = new WalletEntity(wallet);

        WalletEntity saved = walletRepository.save(entity);
        Long walletId = saved.getId();

        ResponseEntity<Void> response = restTemplate.postForEntity(
                "http://localhost:" + port + "/api/wallets/" + walletId + "/deposit?amount=50",
                null,
                Void.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        WalletEntity updated = walletRepository.findById(walletId).orElseThrow();
        assertThat(updated.getBalance()).isEqualByComparingTo(BigDecimal.valueOf(150));
    }

    @Test
    void withdrawShouldDecreaseWalletBalance() {
        Wallet wallet = new Wallet(null, BigDecimal.valueOf(100));
        WalletEntity entity = new WalletEntity(wallet);

        WalletEntity saved = walletRepository.save(entity);
        Long walletId = saved.getId();

        ResponseEntity<Void> response = restTemplate.postForEntity(
                "http://localhost:" + port + "/api/wallets/" + walletId + "/withdraw?amount=50",
                null,
                Void.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        WalletEntity updated = walletRepository.findById(walletId).orElseThrow();
        assertThat(updated.getBalance()).isEqualByComparingTo(BigDecimal.valueOf(50));
    }

    @Test
    void transferShouldMoveFundsBetweenWallets() {
        Wallet source = new Wallet(null, BigDecimal.valueOf(100));
        WalletEntity sourceEntity = walletRepository.save(new WalletEntity(source));
        Wallet target = new Wallet(null, BigDecimal.valueOf(50));
        WalletEntity targetEntity = walletRepository.save(new WalletEntity(target));

        TransferRequest request = new TransferRequest(sourceEntity.getId(), targetEntity.getId(), BigDecimal.valueOf(30));

        ResponseEntity<Void> response = restTemplate.postForEntity(
                "http://localhost:" + port + "/api/wallets/transfer",
                request,
                Void.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        WalletEntity updatedSource = walletRepository.findById(sourceEntity.getId()).orElseThrow();
        WalletEntity updatedTarget = walletRepository.findById(targetEntity.getId()).orElseThrow();

        assertThat(updatedSource.getBalance()).isEqualByComparingTo(BigDecimal.valueOf(70));
        assertThat(updatedTarget.getBalance()).isEqualByComparingTo(BigDecimal.valueOf(80));
    }

    @Test
    void getBalanceShouldReturnCurrentBalance() {
        Wallet wallet = new Wallet(null, BigDecimal.valueOf(200));
        WalletEntity entity = new WalletEntity(wallet);

        WalletEntity saved = walletRepository.save(entity);
        Long walletId = saved.getId();

        ResponseEntity<BigDecimal> response = restTemplate.getForEntity(
                "http://localhost:" + port + "/api/wallets/" + walletId + "/balance",
                BigDecimal.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualByComparingTo(BigDecimal.valueOf(200));
    }

    @Test
    void getHistoricalBalanceShouldReturnCorrectBalance() throws InterruptedException {
        Wallet wallet = new Wallet(null, BigDecimal.ZERO);
        wallet.deposit(BigDecimal.valueOf(100));
        LocalDateTime historicalTime = LocalDateTime.now();
        Thread.sleep(Duration.ofMillis(1));
        wallet.withdraw(BigDecimal.valueOf(30));
        WalletEntity entity = new WalletEntity(wallet);

        WalletEntity saved = walletRepository.save(entity);
        Long walletId = saved.getId();

        ResponseEntity<BigDecimal> response = restTemplate.getForEntity(
                "http://localhost:" + port + "/api/wallets/" + walletId + "/balance/historical?at=" + historicalTime,
                BigDecimal.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualByComparingTo(BigDecimal.valueOf(100));
    }
}
