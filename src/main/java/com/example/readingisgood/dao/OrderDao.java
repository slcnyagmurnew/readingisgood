package com.example.readingisgood.dao;

import com.example.readingisgood.model.Book;
import com.example.readingisgood.model.Order;
import com.example.readingisgood.model.Stats;
import com.example.readingisgood.repository.OrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.*;

@Component
public class OrderDao implements Dao<Order> {

    @Autowired
    OrderRepository orderRepository; // order collection in mongo

    @Autowired
    BookDao bookDao; // necessary for update operation in new order

    private static final Logger logger = LoggerFactory.getLogger(OrderDao.class);

    /**
     * Get all orders in a given interval exist in mongo db
     * Date format: 'yyyy-MM-dd'
     * @param start: start date of the given interval
     * @param end: end date of the given interval
     * @return: list of the orders in a given interval
     */
    public Collection<Order> getOrdersByInterval(Date start, Date end) {
        return orderRepository.getOrderByOrderDate(start, end);
    }

    /**
     * Get monthly statistics of the user
     * @param username: given user
     * @return: list of the monthly statistics of the user
     */
    public List<Stats> getStatistics(String username) {
        return orderRepository.getStats(username);
    }

    /**
     * Get order with using id from mongo db
     * @param value: id of the order
     * @return: order with given id
     */
    @Override
    public Optional<Order> get(String value) {
        return orderRepository.findById(value);
    }

    /**
     * Get all orders belong to user
     * @param condition: username to search in order repository
     * @return: list of the order belong to user
     */
    @Override
    public Collection<Order> getAll(String condition) {
        return orderRepository.getOrdersByUsername(condition);
    }

    /**
     * Save new order
     * Calculate books count and purchase amount for desired order if there is no error
     * If error occurs, throw necessary exception
     * @param order: get order from controller to save into mongo db
     */
    @Override
    public void save(Order order) {
        HashMap<String, Integer> books = order.getBooks();

        for (String name: books.keySet()) {
            Optional<Book> optionalBook = bookDao.get(name);
            if (optionalBook.isEmpty()) { // check if any book in order does not exist
                logger.error(String.format("Book %s does not exists!", name));
                throw new NullPointerException();
            }
            if (books.get(name) < 0) {
                logger.error("Book order value can not be negative!"); // check if count of any book is negative
                throw new IllegalArgumentException();
            }
            Book existingBook = optionalBook.get();
            if (bookDao.checkStockOfBookForOrder(existingBook, books.get(name))) { // check if any of book in order has enough stock to get it
                logger.error(String.format("Book %s has not enough stock!", name));
                throw new IllegalStateException();
            }
        }

        int bookCount = 0;
        double purchasedAmount = 0.0;

        for (String key: books.keySet()) { // loop over books map
            Book book = bookDao.get(key).get(); // no need to optional class, we check existence before
            double price = book.getPrice();

            int count = books.get(key);
            bookCount += count;
            purchasedAmount += (price * count); // multiply with positive value of required book stock

            count *= -1; // it is order operation so book count should be decreased, convert to negative
            book.setStock(count); // set required stock value to use it in update function
            bookDao.update(book);
        }
        order.setBookCount(bookCount); // set calculated book count
        order.setPurchasedAmount(purchasedAmount); // set calculated order purchase amount
        orderRepository.insert(order); // add to mongo
    }

    @Override
    public void update(Order order) {
        // no need but it is from dao interface
    }

    @Override
    public void deleteAll() {
        orderRepository.deleteAll();
    }
}
