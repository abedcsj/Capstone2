package com.example.capstone2.controller;

import com.example.capstone2.dto.CreditDto;
import com.example.capstone2.dto.UserDto;
import com.example.capstone2.security.PrincipalDetails;
import com.example.capstone2.service.BoardService;
import com.example.capstone2.service.CreditService;
import com.example.capstone2.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")

public class AdminController {

    private final UserService userService;
    private final BoardService boardService;
    private final CreditService creditService;

    //사용자 삭제
    @DeleteMapping("/user/{userId}")
    public ResponseEntity<String> deleteUser(@PathVariable Long userId) {
        userService.deleteUserByAdmin(userId);
        return ResponseEntity.ok("관리자의 권한으로 사용자가 삭제되었습니다.");
    }
    //게시글 삭제
    @DeleteMapping("/board/{boardId}")
    public ResponseEntity<String> deleteBoard(@PathVariable Long boardId,
                                              @AuthenticationPrincipal PrincipalDetails principal) {
        boardService.deleteBoardByAdmin(boardId, principal.getUser());
        return ResponseEntity.ok("관리자의 권한으로 게시글이 삭제되었습니다.");
    }
    //사용자 목록 조회
    @GetMapping("/users")
    public ResponseEntity<List<UserDto>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }
    //특정 사용자 조회
    @GetMapping("/user/{userId}")
    public ResponseEntity<UserDto> getUserById(@PathVariable Long userId) {
        return ResponseEntity.ok(userService.getUserById(userId));
    }
    //사용자 크레딧 조회
    @GetMapping("/user/{userId}/credits")
    public ResponseEntity<List<CreditDto>> getUserCredits(@PathVariable Long userId) {
        return ResponseEntity.ok(creditService.getUserCreditsByUserId(userId));
    }
}
