package com.example.capstone2.repository;

import com.example.capstone2.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findEntityGraphRoleTypeById(Long id); // AuthorizationFilter용
    boolean existsByEmail(String email);
}
