package com.example.readingisgood.controller;

import com.example.readingisgood.dao.BookDao;
import com.example.readingisgood.model.Book;
import com.example.readingisgood.payload.MessageResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;

@RestController
@RequestMapping("/api/book")
public class BookController {

    @Autowired
    BookDao bookDao; // access to book repository

    private static final Logger logger = LoggerFactory.getLogger(BookController.class);

    /**
     * Accept adding new book to book collection request, permit admin role
     * @param book: book object for adding operation
     * @return: success message if operation succeeded otherwise error message
     */
    @PostMapping("/add")
    @PreAuthorize("hasRole('ADMIN')")
    ResponseEntity<MessageResponse> addBook(@Valid @RequestBody Book book) {
        try {
            bookDao.save(book); // save into book repository
            logger.info("New book added successfully!");
            return ResponseEntity.ok().body(
                    new MessageResponse("New book added successfully!"));
        } catch (DuplicateKeyException err) {
            logger.error("Book already exists! Try update the book stock.");
            return new ResponseEntity<>(
                    new MessageResponse("Book already exists! Try update the book stock."), HttpStatus.CONFLICT);
        } catch (IllegalArgumentException err) {
            logger.error("Book name must not be null!");
            return new ResponseEntity<>(
                    new MessageResponse("Book name must not be null!"), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Accept update book operation request, permit user and admin role
     * @param name: path variable for getting name of the book to be updated
     * @param book: book object with desired count of the book with stock attribute
     * @return: success message if operation succeeded otherwise error message
     */
    @PatchMapping("/update/{name}")
    @PreAuthorize("hasRole('USER') || hasRole('ADMIN')")
    ResponseEntity<MessageResponse> updateBook(@PathVariable String name, @Valid @RequestBody Book book) {
        try {
            book.setName(name);
            bookDao.update(book); // update with book repository
            logger.info("Book stock updated successfully!");
            return ResponseEntity.ok().body(
                    new MessageResponse("Book stock updated successfully!"));
        } catch (NullPointerException err) { // catch book non-existence
            logger.error("Book does not exists!");
            return new ResponseEntity<>(
                    new MessageResponse("Book does not exists!"), HttpStatus.BAD_REQUEST);
        } catch (IllegalStateException err) { // catch stock of book being below zero condition
            logger.error(String.format("Book %s has not enough stock!", book.getName()));
            return new ResponseEntity<>(
                    new MessageResponse(String.format("Book %s has not enough stock!", book.getName())), HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }


}
