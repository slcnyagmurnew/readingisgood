package com.example.readingisgood.repository;

import com.example.readingisgood.model.Order;
import com.example.readingisgood.model.Stats;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface OrderRepository extends MongoRepository<Order, String> {

    // group by with month to get statistics in monthly
    @Aggregation(pipeline = {
            "{ $match: { 'username': ?0 } }",
            "{ $group: { _id: { $month: '$orderDate' }," +
                    " totalBookCount: { $sum: '$bookCount' }," +
                    " totalPurchasedAmount: { $sum: '$purchasedAmount' }," +
                    " totalOrderCount: { $sum: 1 }}}"
    })
    List<Stats> getStats(String username);

    // get order by id
    @Query("{'_id': ?0}")
    Optional<Order> findById(String id);

    // get orders in a given interval
    @Query("{'orderDate': {'$gte': ?0, '$lt': ?1}}")
    List<Order> getOrderByOrderDate(Date start, Date end);

    // get orders belong to user
    @Query("{'username': ?0}")
    List<Order> getOrdersByUsername(String username);
}
