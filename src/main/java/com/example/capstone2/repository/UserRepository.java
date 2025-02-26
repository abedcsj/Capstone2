package com.example.capstone2.repository;

import com.example.capstone2.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Long> {
    Optional<User> findByEmail(String email); // 이메일로 사용자 조회 (로그인 시 사용)
    boolean existsByEmail(String email); // 이메일 중복 체크
}
