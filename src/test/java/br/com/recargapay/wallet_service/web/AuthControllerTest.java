package br.com.recargapay.wallet_service.web;

import br.com.recargapay.wallet_service.config.MockedBean;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(MockedBean.class)
@WithMockUser(username = "testuser", roles = {"USER"})
public class AuthControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private HttpHeaders headers;

    @BeforeEach
    void setup() {
        headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
    }

    @Test
    void loginShouldReturnAValidToken() {
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("username", "testuser");
        formData.add("password", "123456");

        HttpEntity<MultiValueMap<String, String>> request =
            new HttpEntity<>(formData, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(
            "http://localhost:" + port + "/auth/login",
            request,
            Map.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).containsKey("token");
    }
}
