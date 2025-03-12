package com.example.capstone2.controller;

import com.example.capstone2.dto.BoardDto;
import com.example.capstone2.service.BoardService;
import com.example.capstone2.domain.Category;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/board")
public class BoardController {
    private final BoardService boardService;

    // 📌 게시글 생성 API
    @PostMapping
    public ResponseEntity<String> createBoard(@RequestBody BoardDto boardDto) {
        boardService.createBoard(boardDto);
        return ResponseEntity.ok("게시글이 성공적으로 생성되었습니다.");
    }

    // 📌 게시글 수정 API (본인만 가능)
    @PutMapping("/{boardId}")
    public ResponseEntity<String> updateBoard(@PathVariable Long boardId, @RequestBody BoardDto boardDto) {
        boardService.updateBoard(boardId, boardDto);
        return ResponseEntity.ok("게시글이 성공적으로 수정되었습니다.");
    }

    // 📌 게시글 삭제 API (본인만 가능)
    @DeleteMapping("/{boardId}")
    public ResponseEntity<String> deleteBoard(@PathVariable Long boardId, @RequestParam Long userId) {
        boardService.deleteBoard(boardId, userId);
        return ResponseEntity.ok("게시글이 성공적으로 삭제되었습니다.");
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

    // 📌 게시글 모집 상태 변경 API (모집 완료 ↔ 모집 중)
    @PatchMapping("/{boardId}/toggle-status")
    public ResponseEntity<String> toggleBoardStatus(@PathVariable Long boardId, @RequestParam Long userId) {
        boardService.toggleBoardStatus(boardId, userId);
        return ResponseEntity.ok("게시글 모집 상태가 변경되었습니다.");
    }
}