package com.example.capstone2.controller;

import com.example.capstone2.dto.UserDto;
import com.example.capstone2.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    // 📌 회원가입 API
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody UserDto userDto) {
        userService.register(userDto);
        return ResponseEntity.ok("회원가입이 완료되었습니다.");
    }

    // 📌 로그인 API
    @PostMapping("/login")
    public ResponseEntity<UserDto> login(@RequestParam String email, @RequestParam String password) {
        UserDto userDto = userService.login(email, password);
        return ResponseEntity.ok(userDto);
    }
    // 📌 사용자 정보 수정 API
    @PatchMapping("/{userId}")
    public ResponseEntity<String> update(@PathVariable Long userId, @RequestBody UserDto userDto) {
        userService.updateUser(userId,userDto);
        return ResponseEntity.ok("회원가입이 완료되었습니다.");
    }

    // 📌 사용자 정보 조회 API (비밀번호 제외)
    @GetMapping("/{userId}")
    public ResponseEntity<UserDto> getUserById(@PathVariable Long userId) {
        return ResponseEntity.ok(userService.getUserById(userId));
    }

    // 📌 회원 탈퇴 API (id 값만 필요)
    @DeleteMapping("/{userId}")
    public ResponseEntity<String> deleteUser(@PathVariable Long userId) {
        userService.deleteUser(userId);
        return ResponseEntity.ok("사용자가 삭제되었습니다.");
    }
}
