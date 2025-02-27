package com.example.capstone2.controller;


import com.example.capstone2.domain.User;
import com.example.capstone2.dto.UserDto;
import com.example.capstone2.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    // 📌 회원가입 API-성준
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody UserDto userDto) {
        userService.register(userDto);
        return ResponseEntity.ok("회원가입이 완료되었습니다.");
    }

    // 📌 로그인 API-성준
    @PostMapping("/login")
    public ResponseEntity<UserDto> login(@RequestParam String email, @RequestParam String password) {
        UserDto userDto = userService.login(email, password);
        return ResponseEntity.ok(userDto);
    }

    // 📌 사용자 정보 조회 API-성준
    @GetMapping("/{id}")
    public ResponseEntity<Optional<User>> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    // 📌 회원 탈퇴 API-성준
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok("사용자가 삭제되었습니다.");
    }
}