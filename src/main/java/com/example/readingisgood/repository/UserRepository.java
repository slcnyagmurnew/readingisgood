package com.example.readingisgood.repository;

import com.example.readingisgood.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.Optional;

public interface UserRepository extends MongoRepository<User, String> {

    // get user via username
    @Query("{username: '?0'}")
    Optional<User> findByUsername(String username);

    // if user exists with username return true via mongo query
    @Query(value = "{username: '?0'}", exists = true)
    boolean existsByUsername(String username);

    // if user exists with email return true via mongo query
    @Query(value = "{email: '?0'}", exists = true)
    boolean existsByEmail(String email);
}
