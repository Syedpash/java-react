package com.eyeapss.aiagent.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * User entity — mapped to the "users" table in H2 (in-memory) database.
 * Used by Spring Security to authenticate users and issue JWT tokens.
 *
 * Same JPA annotations you used in TaskManager:
 *   @Entity, @Table, @Id, @GeneratedValue, @Column
 */
@Entity
@Table(name = "users")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // auto-increment ID
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password; // BCrypt hashed — never plain text

    @Column(nullable = false)
    @Builder.Default
    private String role = "ROLE_USER"; // Spring Security role format
}
