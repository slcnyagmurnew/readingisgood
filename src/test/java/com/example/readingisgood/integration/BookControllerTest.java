package com.example.readingisgood.integration;

import com.example.readingisgood.dao.CustomerDao;
import com.example.readingisgood.model.*;
import com.example.readingisgood.payload.LoginRequest;
import com.example.readingisgood.payload.SignUpRequest;
import com.example.readingisgood.repository.BookRepository;
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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.context.WebApplicationContext;
import java.text.SimpleDateFormat;
import java.util.HashSet;
import java.util.Set;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ImportAutoConfiguration(exclude = EmbeddedMongoAutoConfiguration.class)
public class BookControllerTest {

    @Autowired
    TestRestTemplate restTemplate;

    @Autowired
    WebApplicationContext webApplicationContext;

    private HttpHeaders headers;

    @Autowired
    CustomerDao customerDao;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    BookRepository bookRepository;

    private String baseUrl;

    @LocalServerPort
    private int port;

    SimpleDateFormat formatter= new SimpleDateFormat("yyyy-MM-dd"); // date formatter

    @BeforeEach
    public void before() {
        baseUrl = String.format("http://localhost:%d", port);
        Set<String> roles = new HashSet<>();
        Set<Role> role = new HashSet<>();
        roles.add("user");
        roles.add("admin"); // for add request
        role.add(new Role(ERole.ROLE_USER));
        role.add(new Role(ERole.ROLE_ADMIN));
        User testUser = new User("bookUser", "bookUser", "bookUser@example.com", role);
        SignUpRequest signUpRequest = new SignUpRequest();
        signUpRequest.setEmail(testUser.getEmail());
        signUpRequest.setPassword(testUser.getPassword());
        signUpRequest.setUsername(testUser.getUsername());
        signUpRequest.setRoles(roles);

        customerDao.signup(signUpRequest);

        Book book =  new Book("existingBook", 30.00, 50);
        bookRepository.insert(book);

        String loginUrl = String.format("%s/api/customer/login", baseUrl);
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername(testUser.getUsername());
        loginRequest.setPassword(testUser.getPassword());
        String token = "Bearer " + restTemplate.postForObject(loginUrl, loginRequest, String.class);
        this.headers = getHeaders();
        headers.set("Authorization", token);
    }

    @Test
    void addBookTest() {
        Book book = new Book("addedBook", 20.00, 40);
        HttpEntity<Book> jwtEntity = new HttpEntity<>(book, headers);
        ResponseEntity<String> response = restTemplate.exchange(baseUrl + "/api/book/add",
                HttpMethod.POST, jwtEntity, String.class);
        assertThat(response.getBody()).isEqualTo("New book added successfully!");
    }

    @Test
    void updateBookTest() {
        Book book =  new Book(null, 0, -5);
        HttpEntity<Book> jwtEntity = new HttpEntity<>(book, headers);
        ResponseEntity<String> response = restTemplate.exchange(baseUrl + "/api/book/update/existingBook",
                HttpMethod.PUT, jwtEntity, String.class);
        assertThat(response.getBody()).isEqualTo("Book stock updated successfully!");
    }

    private HttpHeaders getHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", MediaType.APPLICATION_JSON_VALUE);
        headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);
        return headers;
    }

    @AfterEach
    void clean() {
        bookRepository.deleteAll();
        customerDao.deleteAll();
    }
}
