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
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.web.context.WebApplicationContext;
import java.util.HashSet;
import java.util.Set;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ImportAutoConfiguration(exclude = EmbeddedMongoAutoConfiguration.class)
public class StatisticsControllerTest {

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
    void before() {
        baseUrl = String.format("http://localhost:%d", port);
        Set<String> roles = new HashSet<>();
        Set<Role> role = new HashSet<>();
        roles.add("user");
        role.add(new Role(ERole.ROLE_USER));
        User testUser = new User("statsUser", "statsUser", "statsUser@example.com", role);
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
    void getStatisticsTest() {
        ResponseEntity<String> response = restTemplate.exchange(baseUrl + "/api/statistics/get",
                HttpMethod.GET, new HttpEntity<>(headers), String.class);
        assertThat(response.getBody()).isEqualTo(String.format("No statistics found for user %s", "statsUser"));
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
