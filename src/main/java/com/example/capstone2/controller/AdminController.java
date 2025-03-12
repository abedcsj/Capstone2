package com.example.capstone2.controller;

import com.example.capstone2.domain.Role;
import com.example.capstone2.domain.User;
import com.example.capstone2.dto.CreditDto;
import com.example.capstone2.dto.UserDto;
import com.example.capstone2.service.BoardService;
import com.example.capstone2.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.capstone2.service.CreditService;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {
    private final UserService userService;
    private final BoardService boardService;
    private final CreditService creditService;

    // 📌 사용자 강제 탈퇴 기능 (관리자 권한 체크는 서비스에서 수행)
    @DeleteMapping("/user/{userId}")
    public ResponseEntity<String> deleteUser(@PathVariable Long userId,@RequestBody User admin) {
        userService.deleteUserByAdmin(userId,admin);
        return ResponseEntity.ok("관리자의 권한으로 사용자가 삭제되었습니다.");
    }

    // 📌 게시판 글 삭제 기능 (관리자 권한 체크는 서비스에서 수행)
    @DeleteMapping("/board/{boardId}")
    public ResponseEntity<String> deleteBoard(@PathVariable Long boardId, @RequestBody User admin) {
        boardService.deleteBoardByAdmin(boardId, admin);
        return ResponseEntity.ok("관리자의 권한으로 게시글이 삭제되었습니다.");
    }

    // 📌 전체 사용자 목록 조회 (비밀번호 제외)
    @GetMapping("/users")
    public ResponseEntity<List<UserDto>> getAllUsers(@RequestBody User admin) {
        return ResponseEntity.ok(userService.getAllUsers(admin));
    }

    // 📌 특정 사용자 상세 정보 조회 (비밀번호 제외)
    @GetMapping("/user/{userId}")
    public ResponseEntity<UserDto> getUserById(@PathVariable Long userId) {
        return ResponseEntity.ok(userService.getUserById(userId));
    }

    // 📌 특정 사용자의 크레딧 내역 조회 (보낸 & 받은 내역 포함)
    @GetMapping("/user/{userId}/credits")
    public ResponseEntity<List<CreditDto>> getUserCredits(@PathVariable Long userId, @RequestBody User admin) {
        if (admin.getRole() != Role.ADMIN) {
            return ResponseEntity.status(403).build();
        }
        return ResponseEntity.ok(creditService.getUserCredits(userId));
    }
}
