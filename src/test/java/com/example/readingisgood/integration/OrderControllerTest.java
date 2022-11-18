package com.example.readingisgood.integration;

import com.example.readingisgood.dao.CustomerDao;
import com.example.readingisgood.model.*;
import com.example.readingisgood.payload.LoginRequest;
import com.example.readingisgood.payload.OrderRequest;
import com.example.readingisgood.payload.SignUpRequest;
import com.example.readingisgood.repository.BookRepository;
import com.example.readingisgood.repository.OrderRepository;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ImportAutoConfiguration(exclude = EmbeddedMongoAutoConfiguration.class)
public class OrderControllerTest {

    @Autowired
    WebApplicationContext webApplicationContext;

    private HttpHeaders headers;

    @Autowired
    CustomerDao customerDao;

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    BookRepository bookRepository;

    private String baseUrl;

    @LocalServerPort
    private int port;

    SimpleDateFormat formatter= new SimpleDateFormat("yyyy-MM-dd"); // date formatter

    @Autowired
    private TestRestTemplate restTemplate;  // web env ile baglantili


    @BeforeEach
    public void before() throws ParseException {
        baseUrl = String.format("http://localhost:%d", port);
        Set<String> roles = new HashSet<>();
        Set<Role> role = new HashSet<>();
        roles.add("user");
        role.add(new Role(ERole.ROLE_USER));
        User testUser = new User("orderUser", "orderUser", "orderUser@example.com", role);
        SignUpRequest signUpRequest = new SignUpRequest();
        signUpRequest.setEmail(testUser.getEmail());
        signUpRequest.setPassword(testUser.getPassword());
        signUpRequest.setUsername(testUser.getUsername());
        signUpRequest.setRoles(roles);

        customerDao.signup(signUpRequest);

        Book book =  new Book("orderBook", 30.00, 50);
        bookRepository.insert(book);

        Order existingOrder = new Order("orderUser", null, 3, 0.0, formatter.parse("2022-11-15"));
        existingOrder.setId("existingOrder");
        orderRepository.insert(existingOrder);

        String loginUrl = String.format("%s/api/customer/login", baseUrl);
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername(testUser.getUsername());
        loginRequest.setPassword(testUser.getPassword());
        String token = "Bearer " + restTemplate.postForObject(loginUrl, loginRequest, String.class);
        this.headers = getHeaders();
        headers.set("Authorization", token);
    }

    @Test
    void addOrderTest() {
        HashMap<String, Integer> books = new HashMap<>();
        books.put("orderBook", 2);
        OrderRequest orderRequest = new OrderRequest();
        orderRequest.setBooks(books);
        HttpEntity<OrderRequest> jwtEntity = new HttpEntity<>(orderRequest, headers);
        ResponseEntity<String> response = restTemplate.exchange(baseUrl + "/api/order/add",
                HttpMethod.POST, jwtEntity, String.class);
        assertThat(response.getBody()).isEqualTo("Order saved successfully!");
    }

    @Test
    void getOrderTest() throws ParseException {
        Order existingOrder = new Order("orderUser", null, 3, 0.0, formatter.parse("2022-11-15"));
        existingOrder.setId("existingOrder");
        ResponseEntity<Order> response = restTemplate.exchange(baseUrl + "/api/order/existingOrder",
                HttpMethod.GET, new HttpEntity<>(headers), Order.class);
        assertThat(response.getBody()).isEqualTo(existingOrder);
    }

    @Test
    void getOrdersByIntervalTest() throws ParseException {
        OrderRequest orderRequest = new OrderRequest();
        Date start = formatter.parse("2022-11-01");
        Date end = formatter.parse("2025-12-01");
        orderRequest.setStart(start);
        orderRequest.setEnd(end);
        HttpEntity<OrderRequest> jwtEntity = new HttpEntity<>(orderRequest, headers);
        ResponseEntity<ArrayList> response = restTemplate.exchange(baseUrl + "/api/order/interval",
                HttpMethod.POST, jwtEntity, ArrayList.class);
        LinkedHashMap actualOrder = (LinkedHashMap) Objects.requireNonNull(response.getBody()).get(0);
        assertThat(actualOrder.get("id")).isEqualTo("existingOrder");
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
        orderRepository.deleteAll();
    }

}
