package com.example.readingisgood.controller;

import com.example.readingisgood.dao.OrderDao;
import com.example.readingisgood.model.Order;
import com.example.readingisgood.payload.OrderRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/order")
public class OrderController {

    @Autowired
    OrderDao orderDao; // necessary for order operations

    SimpleDateFormat formatter= new SimpleDateFormat("yyyy-MM-dd"); // date formatter

    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);

    /**
     * Accept new order saving request
     * Use security context to get username from authentication, permit user role
     * @param orderRequest: get order attributes with order request class
     * @return: if order is saved return success message otherwise return error messages
     * @throws ParseException: parsing order creation date
     */
    @PostMapping("/add")
    @PreAuthorize("hasRole('USER')")
    ResponseEntity<String> addOrder(@Valid @RequestBody OrderRequest orderRequest) throws ParseException {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = userDetails.getUsername(); // get username from context (login required)

        HashMap<String, Integer> books = orderRequest.getBooks();
        try { // create order without count and amount calculations
            Order order = new Order(username, books, 0, 0, formatter.parse(String.valueOf(LocalDate.now())));
            orderDao.save(order);
            logger.info("Order saved successfully!");
            return ResponseEntity.ok().body("Order saved successfully!");
        } catch (NullPointerException err) {
            return ResponseEntity.badRequest().body( // can not order non-available book
                    "One of the books does not exists!");
        } catch (IllegalStateException err) {
            return new ResponseEntity<>( // can not order more than stock of the book
                    "One of the books has not enough stock!", HttpStatus.UNPROCESSABLE_ENTITY);
        } catch (IllegalArgumentException err) {
            return new ResponseEntity<>( // book count for order must be positive number
                    "One of the books does not have legal value!", HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Accept getting order with specific id request
     * Get id from path and use it in dao operation, permit user role
     * @param id: order id
     * @return: order with given id (if exists)
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    ResponseEntity<?> getOrder(@PathVariable String id) {
        Optional<Order> order = orderDao.get(id);
        if (order.isPresent()) { // check existence of order
            logger.info(String.format("Order information: %s", order.get()));
            return new ResponseEntity<>(order.get(), HttpStatus.OK);
        }
        else {
            logger.warn("Order not found!");
            return new ResponseEntity<>("Order not found!", HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Accept listing of orders in given interval request
     * Get all orders belong to any user, permit user and admin role
     * @param orderRequest: get desired date interval from order request class
     * @return: list of the orders within given interval
     */
    @PostMapping("/interval")
    @PreAuthorize("hasRole('ADMIN') || hasRole('USER')")
    ResponseEntity<List<Order>> getOrdersByInterval(@Valid @RequestBody OrderRequest orderRequest) {
        List<Order> orderList = (List<Order>) orderDao.getOrdersByInterval(orderRequest.getStart(), orderRequest.getEnd());
        logger.info("Orders fetched successfully!");
        return new ResponseEntity<>(orderList, HttpStatus.OK);
    }
}
