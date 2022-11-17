package com.example.readingisgood.repository;

import com.example.readingisgood.model.Book;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import java.util.Optional;

public interface BookRepository extends MongoRepository<Book, String> {

    // get book by name (id)
    @Query("{'name': ?0}")
    Optional<Book> findByName(String name);
}
