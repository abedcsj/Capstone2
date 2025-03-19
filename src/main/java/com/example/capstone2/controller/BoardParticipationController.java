package com.example.capstone2.controller;

import com.example.capstone2.dto.BoardParticipationDto;
import com.example.capstone2.service.BoardParticipationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/board-participation")
@RequiredArgsConstructor
public class BoardParticipationController {

    private final BoardParticipationService boardParticipationService;

    // 📌 게시글 참여 신청 API (PENDING 상태로 저장)
    @PostMapping("/request")
    public ResponseEntity<String> requestParticipation(@RequestParam Long boardId, @RequestParam Long userId) {
        try {
            boardParticipationService.requestParticipation(boardId, userId);
            return ResponseEntity.ok("게시글 참여 신청이 완료되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("참여 신청 실패: " + e.getMessage());
        }
    }

    // 📌 게시글 작성자가 참여 승인 API (PENDING → APPROVED)
    @PutMapping("/{participationId}/approve")
    public ResponseEntity<String> approveParticipation(@PathVariable Long participationId) {
        try {
            boardParticipationService.approveParticipation(participationId);
            return ResponseEntity.ok("참여 신청이 승인되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("승인 실패: " + e.getMessage());
        }
    }

    // 📌 게시글 작성자가 참여 거절 API (PENDING → REJECTED)
    @PutMapping("/{participationId}/reject")
    public ResponseEntity<String> rejectParticipation(@PathVariable Long participationId) {
        try {
            boardParticipationService.rejectParticipation(participationId);
            return ResponseEntity.ok("참여 신청이 거절되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("거절 실패: " + e.getMessage());
        }
    }

    // 📌 참가자가 환불 요청 API (APPROVED → REFUNDED)
    @PutMapping("/{participationId}/refund")
    public ResponseEntity<String> requestRefund(@PathVariable Long participationId) {
        try {
            boardParticipationService.requestRefund(participationId);
            return ResponseEntity.ok("환불 요청이 완료되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("환불 요청 실패: " + e.getMessage());
        }
    }

    // 📌 특정 게시글에 참여한 사용자 목록 조회 (APPROVED 상태만)
    @GetMapping("/{boardId}/participants")
    public ResponseEntity<List<BoardParticipationDto>> getParticipants(@PathVariable Long boardId) {
        return ResponseEntity.ok(boardParticipationService.getParticipantsByBoard(boardId));
    }
}
