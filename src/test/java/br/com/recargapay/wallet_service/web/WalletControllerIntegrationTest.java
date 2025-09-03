package br.com.recargapay.wallet_service.web;

import br.com.recargapay.wallet_service.application.port.out.WalletBalanceEventMessagingPort;
import br.com.recargapay.wallet_service.config.MockedBean;
import br.com.recargapay.wallet_service.domain.model.Wallet;
import br.com.recargapay.wallet_service.infrastructure.adapter.in.web.dto.WalletBalanceHistoryRequest;
import br.com.recargapay.wallet_service.infrastructure.adapter.in.web.dto.WalletCreateRequest;
import br.com.recargapay.wallet_service.infrastructure.adapter.in.web.dto.WalletDepositRequest;
import br.com.recargapay.wallet_service.infrastructure.adapter.in.web.dto.WalletResponse;
import br.com.recargapay.wallet_service.infrastructure.adapter.in.web.dto.WalletTransferRequest;
import br.com.recargapay.wallet_service.infrastructure.adapter.in.web.dto.WalletWithdrawRequest;
import br.com.recargapay.wallet_service.infrastructure.adapter.out.persistence.JpaWalletBalanceHistoryRepository;
import br.com.recargapay.wallet_service.infrastructure.adapter.out.persistence.JpaWalletRepository;
import br.com.recargapay.wallet_service.infrastructure.adapter.out.persistence.entity.WalletBalanceHistoryEntity;
import br.com.recargapay.wallet_service.infrastructure.adapter.out.persistence.entity.WalletEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(MockedBean.class)
public class WalletControllerIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private JpaWalletRepository walletRepository;

    @Autowired
    private JpaWalletBalanceHistoryRepository walletBalanceHistoryRepository;

    @Autowired
    private WalletBalanceEventMessagingPort publisher;

    @BeforeEach
    void setup() {
        walletRepository.deleteAll();
        walletBalanceHistoryRepository.deleteAll();
        Mockito.reset(publisher);
        Mockito.doNothing().when(publisher).publish(Mockito.anyLong(), Mockito.any());
    }

    @Test
    void createWalletShouldReturnWalletId() {
        WalletCreateRequest request = new WalletCreateRequest();
        request.setInitialAmount(BigDecimal.valueOf(100));

        ResponseEntity<WalletResponse> response = restTemplate.postForEntity(
            "http://localhost:" + port + "/api/wallets",
            request,
            WalletResponse.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().walletId()).isNotNull();

        Mockito.verify(publisher, Mockito.times(1))
            .publish(Mockito.eq(response.getBody().walletId()), Mockito.any());
    }

    @Test
    void depositShouldIncreaseWalletBalance() {
        WalletEntity wallet = walletRepository.save(new WalletEntity(new Wallet(null, BigDecimal.valueOf(100))));
        Long walletId = wallet.getId();

        WalletDepositRequest request = new WalletDepositRequest();
        request.setAmount(BigDecimal.valueOf(50));

        ResponseEntity<Void> response = restTemplate.postForEntity(
            "http://localhost:" + port + "/api/wallets/" + walletId + "/deposit",
            request,
            Void.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        WalletEntity updated = walletRepository.findById(walletId).orElseThrow();
        assertThat(updated.getBalance()).isEqualByComparingTo(BigDecimal.valueOf(150));

        Mockito.verify(publisher, Mockito.times(1))
            .publish(Mockito.eq(walletId), Mockito.any());
    }

    @Test
    void withdrawShouldDecreaseWalletBalance() {
        WalletEntity wallet = walletRepository.save(new WalletEntity(new Wallet(null, BigDecimal.valueOf(100))));
        Long walletId = wallet.getId();

        WalletWithdrawRequest request = new WalletWithdrawRequest();
        request.setAmount(BigDecimal.valueOf(30));

        ResponseEntity<Void> response = restTemplate.postForEntity(
            "http://localhost:" + port + "/api/wallets/" + walletId + "/withdraw",
            request,
            Void.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        WalletEntity updated = walletRepository.findById(walletId).orElseThrow();
        assertThat(updated.getBalance()).isEqualByComparingTo(BigDecimal.valueOf(70));

        Mockito.verify(publisher, Mockito.times(1))
            .publish(Mockito.eq(walletId), Mockito.any());
    }

    @Test
    void transferShouldMoveFundsBetweenWallets() {
        WalletEntity source = walletRepository.save(new WalletEntity(new Wallet(null, BigDecimal.valueOf(100))));
        WalletEntity target = walletRepository.save(new WalletEntity(new Wallet(null, BigDecimal.valueOf(50))));

        WalletTransferRequest request = new WalletTransferRequest();
        request.setAmount(BigDecimal.valueOf(30));
        request.setDestinationWalletId(target.getId());

        ResponseEntity<Void> response = restTemplate.postForEntity(
            "http://localhost:" + port + "/api/wallets/" + source.getId() + "/transfer",
            request,
            Void.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        WalletEntity updatedSource = walletRepository.findById(source.getId()).orElseThrow();
        WalletEntity updatedTarget = walletRepository.findById(target.getId()).orElseThrow();

        assertThat(updatedSource.getBalance()).isEqualByComparingTo(BigDecimal.valueOf(70));
        assertThat(updatedTarget.getBalance()).isEqualByComparingTo(BigDecimal.valueOf(80));

        Mockito.verify(publisher, Mockito.times(2))
            .publish(Mockito.anyLong(), Mockito.any());
    }

    @Test
    void getBalanceShouldReturnCurrentBalance() {
        WalletEntity wallet = walletRepository.save(new WalletEntity(new Wallet(null, BigDecimal.valueOf(100))));
        Long walletId = wallet.getId();

        ResponseEntity<BigDecimal> response = restTemplate.getForEntity(
            "http://localhost:" + port + "/api/wallets/" + walletId + "/balance",
            BigDecimal.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualByComparingTo(BigDecimal.valueOf(100));
    }

    @Test
    void getHistoricalBalanceShouldReturnCorrectBalance() throws InterruptedException {
        Wallet wallet = new Wallet(null, BigDecimal.ZERO);
        Thread.sleep(Duration.ofMillis(1));

        wallet.deposit(BigDecimal.valueOf(100));

        LocalDateTime historicalTime = wallet.getEvents().getLast().getUpdatedAt();
        Thread.sleep(Duration.ofMillis(1));

        wallet.withdraw(BigDecimal.valueOf(30));

        WalletEntity saved = walletRepository.save(new WalletEntity(wallet));
        Long walletId = saved.getId();

        List<WalletBalanceHistoryEntity> events = walletBalanceHistoryRepository.saveAll(
            wallet.getEvents().stream()
                .map(event -> {
                    WalletBalanceHistoryEntity entity = new WalletBalanceHistoryEntity();
                    entity.setWalletId(saved.getId());
                    entity.setBalance(event.getBalance());
                    entity.setUpdatedAt(event.getUpdatedAt().truncatedTo(ChronoUnit.MILLIS));
                    return entity;
                })
                .toList()
        );
        walletBalanceHistoryRepository.flush();
        Thread.sleep(Duration.ofMillis(1));

        WalletBalanceHistoryRequest request = new WalletBalanceHistoryRequest();
        request.setBalanceAt(historicalTime.truncatedTo(ChronoUnit.MILLIS));

        ResponseEntity<BigDecimal> response = restTemplate.postForEntity(
            "http://localhost:" + port + "/api/wallets/" + walletId + "/balance/history",
            request,
            BigDecimal.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualByComparingTo(BigDecimal.valueOf(100));
    }
}

