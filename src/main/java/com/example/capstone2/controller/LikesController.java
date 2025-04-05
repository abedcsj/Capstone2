package com.example.capstone2.controller;

import com.example.capstone2.dto.LikeDto;
import com.example.capstone2.service.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/likes")
@RequiredArgsConstructor
public class LikesController {

    private final LikeService likeService;

    // 사용자 좋아요  (거래 완료자만 가능)
    @PostMapping("/user")
    public ResponseEntity<String> toggleUserLike(@RequestBody LikeDto likeDto) {
        try {
            likeService.toggleLikeOnUser(likeDto.getUserId(), likeDto.getLikedUserId());
            return ResponseEntity.ok("좋아요 상태가 변경되었습니다.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}