package com.example.capstone2.controller;

import com.example.capstone2.domain.Board;
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

    // 📌 게시판 생성 API-성준
    @PostMapping("/{boardId}")
    public ResponseEntity<String> createBoard(@RequestBody BoardDto boardDto) {
        boardService.createBoard(boardDto);
        return ResponseEntity.ok("게시글이 성공적으로 수정되었습니다.");
    }

    // 📌게시판 수정 API-성준
    @PutMapping("/{boardId}")
    public ResponseEntity<String> updateBoard(@PathVariable Long boardId, @RequestBody BoardDto boardDto) {
        boardService.updateBoard(boardId, boardDto);
        return ResponseEntity.ok("게시글이 성공적으로 수정되었습니다.");
    }

    // 📌게시판 삭제 API-성준
    @DeleteMapping("/{boardId}")
    public ResponseEntity<String> deleteBoard(@PathVariable Long boardId, @RequestBody BoardDto boardDto) {
        boardService.deleteBoard(boardId, boardDto);
        return ResponseEntity.ok("게시글이 성공적으로 삭제되었습니다.");
    }

    // 📌 카테고리별로 게시판 조회 API-성준
    @GetMapping("/category/{category}")
    public ResponseEntity<List<BoardDto>>getBoardsByCategory(@PathVariable Category category){
        return ResponseEntity.ok(boardService.getBoardsByCategory(category));
    }


}
