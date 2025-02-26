package com.example.capstone2.service;


import com.example.capstone2.domain.Role;
import com.example.capstone2.domain.User;
import com.example.capstone2.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    // 📌 회원가입
    public void register(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(Role.USER);
        userRepository.save(user);
    }
    // 📌 로그인 (이메일 & 비밀번호 확인)
    public User login(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("이메일을 찾을 수 없습니다.")); // 값이 없으면 자동으로 exception 처리
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }
        return user;
    }

    // 📌 사용자 정보 조회
    public Optional<User> getUserById(Long userId) {
        return userRepository.findById(userId);
    }

    // 📌 회원 탈퇴
    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
    }

    // 📌 사용자 강제 탈퇴 (관리자 기능)
    public void deleteUserByAdmin(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자를 찾을 수 없습니다."));
        userRepository.delete(user);
    }

    // 📌 전체 사용자 목록 조회 (관리자 기능)
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }


}
