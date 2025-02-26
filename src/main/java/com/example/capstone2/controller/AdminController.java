package com.example.capstone2.controller;

import com.example.capstone2.domain.User;
import com.example.capstone2.service.BoardService;
import com.example.capstone2.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {
    private final UserService userService;
    private final BoardService boardService;

    //  사용자 강제 탈퇴 기능
    @DeleteMapping("/user/{userId}")
    public ResponseEntity<String> deleteUser(@PathVariable Long userId) {
        userService.deleteUserByAdmin(userId);
        return ResponseEntity.ok("관리자의 권한으로 사용자가 삭제되었습니다.");
    }

    // ✅ 게시판 글 삭제 기능
    @DeleteMapping("/board/{boardId}")
    public ResponseEntity<String> deleteBoard(@PathVariable Long boardId) {
        boardService.deleteBoardByAdmin(boardId);
        return ResponseEntity.ok("관리자의 권한으로 게시글이 삭제되었습니다.");
    }

    // ✅ 전체 사용자 목록 조회 (관리자 기능)
    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    // ✅ 특정 사용자 상세 정보 조회 (관리자 기능)
    @GetMapping("/user/{userId}")
    public ResponseEntity<Optional<User>> getUserById(@PathVariable Long userId) {
        return ResponseEntity.ok(userService.getUserById(userId));
    }
}