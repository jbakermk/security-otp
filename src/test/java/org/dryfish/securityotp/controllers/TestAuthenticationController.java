package org.dryfish.securityotp.controllers;

import org.apache.commons.codec.EncoderException;
import org.dryfish.securityotp.Tokens;
import org.dryfish.securityotp.database.User;
import org.dryfish.securityotp.database.UserRepository;
import org.dryfish.securityotp.service.UserService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpServerErrorException;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(
    webEnvironment = WebEnvironment.RANDOM_PORT,
    properties = { "spring.jpa.hibernate.ddl-auto=create" }
)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TestAuthenticationController {

    private static final String AUTHENTICATE_USERPASS_URL = "http://localhost:%s/otp/authenticate/%s";
    private static final String AUTHENTICATE_TOKEN_URL = "http://localhost:%s/otp/authenticate/token/%s";
    private static final String USERNAME = "user1";
    private static final String PASSWORD = "password1";

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @BeforeAll
    public void createTestUser() {
        userRepository.deleteAll();

        User user= userService.register(USERNAME, PASSWORD, Tokens.SECRET);
        assertThat(userService).isNotNull();
    }

    // Generate at token from time now + given seconds
    private String generateToken(int seconds) throws EncoderException {
        LocalDateTime localDate = LocalDateTime.now();
        return Tokens.generateTotpBySecret(localDate.plusSeconds(seconds).atZone(ZoneId.systemDefault()).toInstant());
    }

    // Service returns JSON strings
    private String status(AuthenticationController.AuthenticationStatus status) {
        return new StringBuilder("\"").append(status.toString()).append("\"").toString();
    }

    @Test
    public void failedAuthentication() throws Exception {
        String url= String.format(AUTHENTICATE_USERPASS_URL, port, USERNAME);
        assertThat(this.restTemplate.postForObject(url,
                new HttpEntity<>(Map.of("password", "abc")),
                String.class)).isEqualTo(status(AuthenticationController.AuthenticationStatus.FAILED));
    }

    @Test
    public void successfulAuthentication() throws Exception {
        String url= String.format(AUTHENTICATE_USERPASS_URL, port, USERNAME);
        assertThat(this.restTemplate.postForObject(url,
                new HttpEntity<>(Map.of("password", PASSWORD)),
                String.class)).isEqualTo(status(AuthenticationController.AuthenticationStatus.REQUIRE_TOKEN_CHECK));
    }

    @Test
    public void authenticationWithInvalidToken() throws Exception {
        String url= String.format(AUTHENTICATE_TOKEN_URL, port, USERNAME);
        assertThat(this.restTemplate.postForObject(url,
                new HttpEntity<>(Map.of("token", generateToken(31))),
                String.class)).isEqualTo(status(AuthenticationController.AuthenticationStatus.REQUIRE_TOKEN_CHECK));
    }

    @Test
    public void authenticationWithNoToken() throws Exception {
        String url= String.format(AUTHENTICATE_TOKEN_URL, port, USERNAME);
        assertThat(this.restTemplate.postForObject(url,
                new HttpEntity<>(Map.of("token", "")),
                String.class)).isEqualTo(status(AuthenticationController.AuthenticationStatus.REQUIRE_TOKEN_CHECK));
    }

    @Test
    public void authenticationWithValidToken() throws Exception {
        String url= String.format(AUTHENTICATE_TOKEN_URL, port, USERNAME);
        assertThat(this.restTemplate.postForObject(url,
                new HttpEntity<>(Map.of("token", generateToken(0))),
                String.class)).isEqualTo(status(AuthenticationController.AuthenticationStatus.AUTHENTICATED));
    }

    @Test
    public void validationFailureNoUsername() throws Exception {
        try {
            String url= String.format(AUTHENTICATE_USERPASS_URL, port, "  ");
            this.restTemplate.postForObject(url,
                    new HttpEntity<>(Map.of("password", "abc")),
                    String.class);
        } catch (HttpServerErrorException e) {
            assertThat(e.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}