package com.example.capstone2.controller;

import com.example.capstone2.dto.BoardParticipationDto;
import com.example.capstone2.service.BoardParticipationService;
import com.example.capstone2.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/board-participation")
@RequiredArgsConstructor
public class BoardParticipationController {

    private final BoardParticipationService boardParticipationService;

    // 📌 참가자가 게시글 참여 신청 API (PENDING 상태로 저장)
    @PostMapping("/request")
    public ResponseEntity<String> requestParticipation(@RequestParam Long boardId, @AuthenticationPrincipal User user) {
        try {
            boardParticipationService.requestParticipation(boardId, user);
            return ResponseEntity.ok("게시글 참여 신청이 완료되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("참여 신청 실패: " + e.getMessage());
        }
    }

    // 📌 참가자가 환불 요청 API (APPROVED → REFUNDED)
    @PutMapping("/{participationId}/refund")
    public ResponseEntity<String> requestRefund(@PathVariable Long participationId, @AuthenticationPrincipal User user) {
        try {
            boardParticipationService.requestRefund(participationId, user);
            return ResponseEntity.ok("환불 요청이 완료되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("환불 요청 실패: " + e.getMessage());
        }
    }
}
