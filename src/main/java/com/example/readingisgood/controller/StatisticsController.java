package com.example.readingisgood.controller;

import com.example.readingisgood.dao.OrderDao;
import com.example.readingisgood.model.Stats;
import com.example.readingisgood.payload.MessageResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/api/statistics")
public class StatisticsController {

    @Autowired
    OrderDao orderDao; // necessary for operations with monthly data

    private static final Logger logger = LoggerFactory.getLogger(StatisticsController.class);

    /**
     * Accept show monthly statistics of the user request, permit user role
     * Get username from security context and give dao function as a parameter
     * @return: list of the statistics of the user
     */
    @GetMapping("/get")
    @PreAuthorize("hasRole('USER')")
    ResponseEntity<?> getStatistics() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = userDetails.getUsername();

        List<Stats> stats = orderDao.getStatistics(username);
        if (stats.isEmpty()) { // check if user has no stats (no order)
            logger.warn(String.format("No statistics found for user %s", username));
            return new ResponseEntity<>(new MessageResponse(String.format("No statistics found for user %s", username)), HttpStatus.UNPROCESSABLE_ENTITY);
        }
        else {
            logger.info(String.format("Statistics fetched for user %s", username));
            return new ResponseEntity<>(stats, HttpStatus.OK);
        }
    }
}
