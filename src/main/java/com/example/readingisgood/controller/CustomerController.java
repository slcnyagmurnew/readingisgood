package com.example.readingisgood.controller;

import com.example.readingisgood.dao.CustomerDao;
import com.example.readingisgood.dao.OrderDao;
import com.example.readingisgood.model.Order;
import com.example.readingisgood.payload.LoginRequest;
import com.example.readingisgood.payload.MessageResponse;
import com.example.readingisgood.payload.SignUpRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/customer")
public class CustomerController {

    @Autowired
    CustomerDao customerDao; // necessary for customer authentication operations

    @Autowired
    OrderDao orderDao; // necessary for getting orders

    private static final Logger logger = LoggerFactory.getLogger(CustomerController.class);

    /**
     * Accept getting all orders belong to given user request, permit user role
     * @return: list of the orders belong to user, if no order return message
     */
    @GetMapping("/orders")
    @PreAuthorize("hasRole('USER')")
    ResponseEntity<?> getOrders() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = userDetails.getUsername(); // get username from context (login required)

        List<Order> orders = (List<Order>) orderDao.getAll(username);
        if (orders.isEmpty()) { // check if list is empty
            return new ResponseEntity<>(new MessageResponse(String.format("There is no order belongs to user %s", username)), HttpStatus.UNPROCESSABLE_ENTITY);
        }
        return new ResponseEntity<>(orders, HttpStatus.OK);
    }

    /**
     * Accept customer or admin login request
     * Login with correct username and password, permit without authentication
     * @param loginRequest: get login attributes (username and password) from login request class
     * @return: success message and token if operation succeeded otherwise bad request
     */
    @PostMapping("/login")
    public ResponseEntity<MessageResponse> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        String jwt = customerDao.login(loginRequest);
        if (jwt != null) {
            logger.info("JWT created!");
            return ResponseEntity.ok(new MessageResponse(jwt));
        }
        else {
            logger.error("JWT can not be created!");
            return new ResponseEntity<>(new MessageResponse("JWT can not be created!"), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Accept logout request
     * Remove user information from security context, permit user and admin roles
     * @return: 200
     */
    @GetMapping("/logout")
    @PreAuthorize("hasRole('ADMIN') || hasRole('USER')")
    public ResponseEntity<?> unauthenticateUser() {
        SecurityContextHolder.clearContext();
        return new ResponseEntity<>(null, HttpStatus.OK);
    }

    /**
     * Accept new user with post request
     * Sign up to system with restrictions (no duplication in username or email), permit without authentication
     * It is accepted to write roles into request body or being null
     * Get request body via SignUpRequest class
     */
    @PostMapping("/signup")
    public ResponseEntity<MessageResponse> registerUser(@Valid @RequestBody SignUpRequest signUpRequest) {
        try {
            customerDao.signup(signUpRequest);
            logger.info("User registered successfully!");
            return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
        } catch (DuplicateKeyException err) {
            logger.error(err.getMessage());
            return new ResponseEntity<>(new MessageResponse(err.getMessage()), HttpStatus.CONFLICT);
        }
    }
}
