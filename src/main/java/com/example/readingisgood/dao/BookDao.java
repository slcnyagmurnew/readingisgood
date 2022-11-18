package com.example.readingisgood.dao;

import com.example.readingisgood.model.Book;
import com.example.readingisgood.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Component;
import java.util.Collection;
import java.util.Optional;

@Component
public class BookDao implements Dao<Book> {

    @Autowired
    BookRepository bookRepository; // book collection in mongo

    /**
     * Check if stock of the book is available or not for ordering
     * @param book: book object that will be changed
     * @param stock: required count of the book in an order
     * @return: true if stock is NOT available otherwise false
     */
    public boolean checkStockOfBookForOrder(Book book, int stock) {
        return (book.getStock() - stock < 0);
    }

    /**
     * Check if stock of the book is available for updating book stock manually
     * @param book: book object to be updated
     * @param stock: value for updating book
     * @return: true if stock is NOT available otherwise false
     */
    public boolean checkStockOfBook(Book book, int stock) {
        return (book.getStock() + stock < 0);
    }

    /**
     * Get book from mongo
     * @param value: name of the book
     * @return: book object if exists in mongo otherwise null
     */
    @Override
    public Optional<Book> get(String value) {
        return bookRepository.findByName(value);
    }

    @Override
    public Collection<Book> getAll(String condition) {
        // no need but it is from dao interface
        return null;
    }

    /**
     * Insert operation for adding book
     * @param o: new book object to insert mongo
     * @throws DuplicateKeyException: name of the book duplication (it represents as a key in mongo)
     */
    @Override
    public void save(Book o) throws DuplicateKeyException {
        if (o.getName() == null) {
            throw new IllegalArgumentException("Book name must not be null!");
        }
        if (o.getStock() < 0)
            throw new IllegalArgumentException("Book stock should be greater than zero!");
        bookRepository.insert(o); // use NOT save to prevent duplication
    }

    /**
     * Update operation for book stock (both decreasing and increasing)
     * Use negative number for order
     * Use positive number for increasing stock
     * @param o: book object to be updated, use name of the book
     */
    @Override
    public void update(Book o) {
        Optional<Book> optionalBook = get(o.getName());
        if (optionalBook.isEmpty()) { // check if book exists
            throw new NullPointerException();
        }
        optionalBook.ifPresent(book -> {
            if (checkStockOfBook(book, o.getStock())) // check if book is available for ordering
                throw new IllegalStateException();
            else {
                int newStock = book.getStock() + o.getStock();
                book.setStock(newStock); // set new stock
                bookRepository.save(book); // save into mongo
            }
        });
    }

    @Override
    public void deleteAll() {
        bookRepository.deleteAll();
    }
}
