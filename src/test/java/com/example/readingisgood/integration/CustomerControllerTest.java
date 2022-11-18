package com.example.readingisgood.integration;

import com.example.readingisgood.dao.CustomerDao;
import com.example.readingisgood.model.ERole;
import com.example.readingisgood.model.Role;
import com.example.readingisgood.model.User;
import com.example.readingisgood.payload.LoginRequest;
import com.example.readingisgood.payload.SignUpRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.embedded.EmbeddedMongoAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.web.context.WebApplicationContext;
import static org.assertj.core.api.Assertions.assertThat;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ImportAutoConfiguration(exclude = EmbeddedMongoAutoConfiguration.class)
public class CustomerControllerTest {

    @Autowired
    WebApplicationContext webApplicationContext;

    @Autowired
    TestRestTemplate restTemplate;

    private HttpHeaders headers;

    @Autowired
    CustomerDao customerDao;

    private String baseUrl;

    @LocalServerPort
    private int port;

    @BeforeEach
    public void before() {
        baseUrl = String.format("http://localhost:%d", port);
        Set<String> roles = new HashSet<>();
        Set<Role> role = new HashSet<>();
        roles.add("user");
        role.add(new Role(ERole.ROLE_USER));
        User testUser = new User("user", "user", "user@example.com", role);
        SignUpRequest signUpRequest = new SignUpRequest();
        signUpRequest.setEmail(testUser.getEmail());
        signUpRequest.setPassword(testUser.getPassword());
        signUpRequest.setUsername(testUser.getUsername());
        signUpRequest.setRoles(roles);

        customerDao.signup(signUpRequest);

        String loginUrl = String.format("%s/api/customer/login", baseUrl);
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername(testUser.getUsername());
        loginRequest.setPassword(testUser.getPassword());
        String token = "Bearer " + restTemplate.postForObject(loginUrl, loginRequest, String.class);
        this.headers = getHeaders();
        headers.set("Authorization", token);
    }

    @Test
    void loginTest() {
        String loginUrl = String.format("%s/api/customer/login", baseUrl);
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("user");
        loginRequest.setPassword("user");
        String token = "Bearer " + restTemplate.postForObject(loginUrl, loginRequest, String.class);
        assertThat(token.contains("ey")).isTrue();
    }

    @Test
    void signUpTest() {
        Set<String> roles = new HashSet<>();
        Set<Role> role = new HashSet<>();
        roles.add("user");
        role.add(new Role(ERole.ROLE_USER));
        User testUser = new User("signUpUser", "signUpUser", "signUpUser@example.com", role);
        SignUpRequest signUpRequest = new SignUpRequest();
        signUpRequest.setEmail(testUser.getEmail());
        signUpRequest.setPassword(testUser.getPassword());
        signUpRequest.setUsername(testUser.getUsername());
        signUpRequest.setRoles(roles);

        HttpEntity<SignUpRequest> jwtEntity = new HttpEntity<>(signUpRequest, headers);
        ResponseEntity<String> response = restTemplate.exchange(baseUrl + "/api/customer/signup",
                HttpMethod.POST, jwtEntity, String.class);
        assertThat(Objects.requireNonNull(response.getBody())).isEqualTo("User registered successfully!");
    }

    @Test
    void getOrdersTest() {
        ResponseEntity<String> response = restTemplate.exchange(baseUrl + "/api/customer/orders",
                HttpMethod.GET, new HttpEntity<>(headers), String.class);
        assertThat(response.getBody()).isEqualTo("There is no order belongs to user %s", "user");
    }

    @Test
    void logoutTest() {
        // can not do this test because the data will be lost
    }

    private HttpHeaders getHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", MediaType.APPLICATION_JSON_VALUE);
        headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);
        return headers;
    }

    @AfterEach
    void clean() {
        customerDao.deleteAll();
    }
}
