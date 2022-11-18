package com.example.readingisgood.unit;

import com.example.readingisgood.dao.BookDao;
import com.example.readingisgood.dao.CustomerDao;
import com.example.readingisgood.dao.OrderDao;
import com.example.readingisgood.model.Book;
import com.example.readingisgood.model.Order;
import com.example.readingisgood.model.Stats;
import com.example.readingisgood.model.User;
import com.example.readingisgood.payload.SignUpRequest;
import com.example.readingisgood.repository.BookRepository;
import com.example.readingisgood.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.embedded.EmbeddedMongoAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ImportAutoConfiguration(exclude = EmbeddedMongoAutoConfiguration.class)
public class OrderDaoTest {

    @Autowired
    private OrderDao orderDao;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookRepository bookRepository;

    SimpleDateFormat formatter= new SimpleDateFormat("yyyy-MM-dd"); // date formatter

    @BeforeEach
    public void before() throws ParseException {
        Book book =  new Book("orderBook", 30.99, 50);
        bookRepository.insert(book);
        User user = new User("orderUser", "orderUser", "orderUser@example.com", null);
        userRepository.insert(user);
        HashMap<String, Integer> books = new HashMap<>();
        books.put("orderBook", 3);
        Order order = new Order("orderUser", books, 0, 0.0, formatter.parse("2022-11-15"));
        order.setId("orderId");
        orderDao.save(order);
    }

    @Test
    void getTest() {
        orderDao.get("orderId").ifPresent(actualOrder -> assertThat(actualOrder.getId()).isEqualTo("orderId"));
    }

    @Test
    void getOrdersByIntervalTest() throws ParseException {
        Date start = formatter.parse("2022-11-10");
        Date end = formatter.parse("2022-12-10");
        List<Order> orders = (List<Order>) orderDao.getOrdersByInterval(start, end);
        assertThat(orders.get(0).getOrderDate()).isEqualTo("2022-11-15");
    }

    @Test
    void getAllTest() {
        String username = "orderUser";
        List<Order> orders = (List<Order>) orderDao.getAll(username);
        assertThat(orders.get(0).getUsername()).isEqualTo(username);
    }

    @Test
    void getStatisticsTest() {
        String username = "orderUser";
        List<Stats> stats = orderDao.getStatistics(username);
        Stats expectedStats = new Stats("11", 3, 92.97, 1);
        assertThat(stats.get(0)).isEqualTo(expectedStats);
    }

    @Test
    void saveTest() throws ParseException {
        HashMap<String, Integer> books = new HashMap<>();
        books.put("orderBook", 4);
        Order order = new Order("orderUser", books, 0, 0.0, formatter.parse("2022-11-15"));
        order.setId("newOrderId");
        orderDao.save(order);
        orderDao.get("newOrderId").ifPresent(actualOrder -> assertThat(actualOrder.getId()).isEqualTo(order.getId()));
    }

    @AfterEach
    public void clean() {
        userRepository.deleteAll();
        bookRepository.deleteAll();
        orderDao.deleteAll();
    }
}
