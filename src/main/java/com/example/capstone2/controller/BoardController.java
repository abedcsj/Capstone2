package com.example.capstone2.controller;

import com.example.capstone2.dto.BoardDto;
import com.example.capstone2.dto.BoardParticipationDto;
import com.example.capstone2.service.BoardParticipationService;
import com.example.capstone2.service.BoardService;
import com.example.capstone2.domain.Category;
import com.example.capstone2.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/board")
public class BoardController {
    private final BoardService boardService;

    // 📌 게시글 생성 API
    @PostMapping
    public ResponseEntity<String> createBoard(@RequestBody BoardDto boardDto, @AuthenticationPrincipal User user) {
        boardService.createBoard(boardDto, user);
        return ResponseEntity.ok("게시글이 성공적으로 생성되었습니다.");
    }

    // 📌 게시글 수정 API (본인만 가능)
    @PutMapping("/{boardId}")
    public ResponseEntity<String> updateBoard(@PathVariable Long boardId, @RequestBody BoardDto boardDto, @AuthenticationPrincipal User user) {
        boardService.updateBoard(boardId, boardDto, user);
        return ResponseEntity.ok("게시글이 성공적으로 수정되었습니다.");
    }

    // 📌 게시글 삭제 API (본인만 가능)
    @DeleteMapping("/{boardId}")
    public ResponseEntity<String> deleteBoard(@PathVariable Long boardId, @AuthenticationPrincipal User user) {
        boardService.deleteBoard(boardId, user);
        return ResponseEntity.ok("게시글이 성공적으로 삭제되었습니다.");
    }

    // 📌 게시글 모집 상태 변경 API (모집 완료 ↔ 모집 중)
    @PatchMapping("/{boardId}/toggle-status")
    public ResponseEntity<String> toggleBoardStatus(@PathVariable Long boardId, @AuthenticationPrincipal User user) {
        boardService.toggleBoardStatus(boardId, user);
        return ResponseEntity.ok("게시글 모집 상태가 변경되었습니다.");
    }

    // 📌 게시글 참여 승인 API (게시글 작성자만 승인 가능)
    @PostMapping("/participation/{participationId}/approve")
    public ResponseEntity<String> approveParticipation(@PathVariable Long participationId, @AuthenticationPrincipal User user) {
        boardService.approveParticipation(participationId, user);
        return ResponseEntity.ok("참여 요청이 승인되었습니다.");
    }

    // 📌 게시글 참여 거절 API (게시글 작성자만 거절 가능)
    @PostMapping("/participation/{participationId}/reject")
    public ResponseEntity<String> rejectParticipation(@PathVariable Long participationId, @AuthenticationPrincipal User user) {
        boardService.rejectParticipation(participationId, user);
        return ResponseEntity.ok("참여 요청이 거절되었습니다.");
    }

    // 📌 카테고리별 게시글 조회 API (최신순 정렬)
    @GetMapping("/category/{category}")
    public ResponseEntity<List<BoardDto>> getBoardsByCategory(@PathVariable Category category) {
        return ResponseEntity.ok(boardService.getBoardsByCategory(category));
    }

    // 📌 게시글 상세 조회 API
    @GetMapping("/{boardId}")
    public ResponseEntity<BoardDto> getBoardById(@PathVariable Long boardId) {
        return ResponseEntity.ok(boardService.getBoardById(boardId));
    }

    // 📌 특정 게시글에 참여한 사용자 목록 조회 (APPROVED 상태만)
    @GetMapping("/{boardId}/participants")
    public ResponseEntity<List<BoardParticipationDto>> getParticipantsByBoard(@PathVariable Long boardId) {
        return ResponseEntity.ok(boardService.getParticipantsByBoard(boardId));
    }

}
