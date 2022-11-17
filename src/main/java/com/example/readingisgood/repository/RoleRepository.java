package com.example.readingisgood.repository;

import com.example.readingisgood.model.ERole;
import com.example.readingisgood.model.Role;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import java.util.Optional;

public interface RoleRepository extends MongoRepository<Role, String> {

    // get role by name
    @Query("{name: '?0'}")
    Optional<Role> findByName(ERole name);
}
