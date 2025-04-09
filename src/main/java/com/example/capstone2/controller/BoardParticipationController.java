package com.example.capstone2.controller;

import com.example.capstone2.dto.BoardParticipationDto;
import com.example.capstone2.repository.BoardParticipationRepository;
import com.example.capstone2.security.PrincipalDetails;
import com.example.capstone2.service.BoardParticipationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/board-participation")
@RequiredArgsConstructor
public class BoardParticipationController {

    private final BoardParticipationService boardParticipationService;

    // 📌 참가자가 게시글 참여 신청 API (PENDING 상태로 저장)
    @PostMapping("/request")
    public ResponseEntity<?> requestParticipation(@RequestParam Long boardId,
                                                  @AuthenticationPrincipal PrincipalDetails principal) {

        try {
            BoardParticipationDto dto = boardParticipationService.requestParticipation(boardId, principal.getUser());
            return ResponseEntity.ok(dto); // ✅ 성공 시 DTO 반환
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("참여 신청 실패: " + e.getMessage());
        }
    }

    // 📌 참가자가 환불 요청 API (APPROVED → REFUNDED)
    @PutMapping("/{participationId}/refund")
    public ResponseEntity<?> requestRefund(@PathVariable Long participationId,
                                           @AuthenticationPrincipal PrincipalDetails principal) {
        try {
            BoardParticipationDto dto = boardParticipationService.requestRefund(participationId, principal.getUser());
            return ResponseEntity.ok(dto); // ✅ 성공 시 DTO 반환
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("환불 요청 실패: " + e.getMessage());
        }
    }
}
