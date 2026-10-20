
package com.example.demo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.example.demo.model.User;

public interface UserRepository extends MongoRepository<User, String> {

    List<User> findByRole(String role);

    Optional<User> findFirstByRoleAndSpecializationOrderByRatingDesc(
            String role,
            String specialization
    );

    Optional<User> findByEmail(String email);

boolean existsByEmail(String email);
}
