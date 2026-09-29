package com.eyeapss.aiagent.repository;

import com.eyeapss.aiagent.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * UserRepository — Spring Data JPA repository for User entity.
 * Spring auto-generates the SQL for findByUsername() from the method name.
 * Returns Optional<User> — forces you to handle "user not found" case.
 */
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    boolean existsByUsername(String username);
}
