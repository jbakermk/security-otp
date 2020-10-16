package org.dryfish.securityotp.controllers;

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

import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(
        webEnvironment = WebEnvironment.RANDOM_PORT,
        properties = { "spring.jpa.hibernate.ddl-auto=create" }
)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TestRegistrationController {

    private static final String URL= "http://localhost:%s/otp/registration/%s";
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
    }

    @Test
    public void noPasswordSupplied() throws Exception {
        String url= String.format(URL, port, USERNAME);
        try {
            this.restTemplate.postForObject(url, new HttpEntity<>(Map.of()), String.class);
        } catch (HttpServerErrorException e) {
            assertThat(e.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }
    }

    @Test
    public void successfulAuthentication() throws Exception {
        String url= String.format(URL, port, USERNAME);
        assertThat(this.restTemplate.postForObject(url,
                new HttpEntity<>(Map.of("password", PASSWORD)),
                String.class)).startsWith("data:");

        Optional<User> user = userService.findUser(USERNAME, PASSWORD);
        assertThat(user.isPresent());
        assertThat(user.get().getLogin()).isEqualTo(USERNAME);
    }

    @Test
    public void validationFailureNoUsername() throws Exception {
        try {
            String url= String.format(URL, port, "  ");
            this.restTemplate.postForObject(url,
                    new HttpEntity<>(Map.of("password", "abc")),
                    String.class);
        } catch (HttpServerErrorException e) {
            assertThat(e.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}