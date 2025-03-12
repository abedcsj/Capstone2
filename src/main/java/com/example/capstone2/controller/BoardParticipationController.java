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
    @PostMapping("/{boardId}/request")
    public ResponseEntity<String> requestParticipation(@PathVariable Long boardId, @RequestParam Long userId) {
        boardParticipationService.requestParticipation(boardId, userId);
        return ResponseEntity.ok("게시글 참여 신청이 완료되었습니다.");
    }

    // 📌 게시글 작성자가 참여 승인 API (PENDING → APPROVED)
    @PutMapping("/{participationId}/approve")
    public ResponseEntity<String> approveParticipation(@PathVariable Long participationId) {
        boardParticipationService.approveParticipation(participationId);
        return ResponseEntity.ok("참여 신청이 승인되었습니다.");
    }

    // 📌 게시글 작성자가 참여 거절 API (PENDING → REJECTED)
    @PutMapping("/{participationId}/reject")
    public ResponseEntity<String> rejectParticipation(@PathVariable Long participationId) {
        boardParticipationService.rejectParticipation(participationId);
        return ResponseEntity.ok("참여 신청이 거절되었습니다.");
    }

    // 📌 게시글 참여 후 서비스 진행 API (APPROVED → 진행 중)
    @PostMapping("/{boardId}/join")
    public ResponseEntity<String> joinBoard(@PathVariable Long boardId, @RequestParam Long userId) {
        boardParticipationService.joinBoard(boardId, userId);
        return ResponseEntity.ok("참여 요청이 완료되었습니다.");
    }

    // 📌 서비스 완료 후 참여자가 확인 (서비스 완료 & 크레딧 지급)
    @PutMapping("/{participationId}/complete")
    public ResponseEntity<String> completeService(@PathVariable Long participationId) {
        boardParticipationService.completeService(participationId);
        return ResponseEntity.ok("서비스가 완료되었습니다.");
    }

    // 📌 특정 게시글에 참여한 사용자 목록 조회 (APPROVED 상태만)
    @GetMapping("/{boardId}/participants")
    public ResponseEntity<List<BoardParticipationDto>> getParticipants(@PathVariable Long boardId) {
        return ResponseEntity.ok(boardParticipationService.getParticipantsByBoard(boardId));
    }
}
