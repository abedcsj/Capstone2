package com.example.capstone2.controller;

import com.example.capstone2.dto.BoardParticipationDto;
import com.example.capstone2.service.BoardParticipationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/board-participation")
public class BoardParticipationController {
    private final BoardParticipationService boardParticipationService;

    // 📌 게시글 참여 신청
    @PostMapping("/{boardId}/join")
    public ResponseEntity<String> joinBoard(@PathVariable Long boardId, @RequestParam Long userId) {
        boardParticipationService.requestParticipation(boardId, userId);
        return ResponseEntity.ok("참여 신청이 완료되었습니다.");
    }

    // 📌 게시글 작성자가 참여 승인
    @PutMapping("/{participationId}/approve")
    public ResponseEntity<String> approveParticipation(@PathVariable Long participationId) {
        boardParticipationService.approveParticipation(participationId);
        return ResponseEntity.ok("참여 요청이 승인되었습니다.");
    }

    // 📌 서비스 완료 후 참여자가 확인
    @PutMapping("/{participationId}/complete")
    public ResponseEntity<String> completeService(@PathVariable Long participationId) {
        boardParticipationService.completeService(participationId);
        return ResponseEntity.ok("서비스가 완료되었습니다.");
    }

    // 📌 특정 게시글에 참여한 사용자 목록 조회
    @GetMapping("/{boardId}/participants")
    public ResponseEntity<List<BoardParticipationDto>> getParticipants(@PathVariable Long boardId) {
        return ResponseEntity.ok(boardParticipationService.getParticipantsByBoard(boardId));
    }
}
