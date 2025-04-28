package com.example.capstone2.controller;

import com.example.capstone2.dto.BoardDto;
import com.example.capstone2.security.PrincipalDetails;
import com.example.capstone2.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/boards")
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;


    // 게시글 작성
    @PostMapping
    public ResponseEntity<String> createBoard(@AuthenticationPrincipal PrincipalDetails principal,
                                              @RequestBody BoardDto boardDto) {
        boardService.createBoard(principal.getUser().getId(), boardDto);
        return ResponseEntity.ok("게시글이 등록되었습니다.");
    }

    // 게시글 수정
    @PatchMapping("/{boardId}")
    public ResponseEntity<String> updateBoard(@AuthenticationPrincipal PrincipalDetails principal,
                                              @PathVariable Long boardId,
                                              @RequestBody BoardDto boardDto) {
        boardService.updateBoard(principal.getUser().getId(), boardId, boardDto);
        return ResponseEntity.ok("게시글이 수정되었습니다.");
    }

    // 게시글 삭제
    @DeleteMapping("/{boardId}")
    public ResponseEntity<String> deleteBoard(@AuthenticationPrincipal PrincipalDetails principal,
                                              @PathVariable Long boardId) {
        boardService.deleteBoard(principal.getUser().getId(), boardId);
        return ResponseEntity.ok("게시글이 삭제되었습니다.");
    }

    // 게시글 단건 조회
    @GetMapping("/{boardId}")
    public ResponseEntity<BoardDto> getBoard(@PathVariable Long boardId) {
        return ResponseEntity.ok(boardService.getBoardById(boardId));
    }


    // 전체 게시글 조회 (페이징 적용)

    @GetMapping
    public ResponseEntity<List<BoardDto>> getAllBoards(@RequestParam(defaultValue = "0") int page) {
        return ResponseEntity.ok(boardService.getBoardsPaged(page));
    }


    @PatchMapping("/{boardId}/toggle-status")
    public ResponseEntity<String> toggleBoardStatus(@PathVariable Long boardId,
                                                    @AuthenticationPrincipal PrincipalDetails principal) {
        boardService.toggleClosed(principal.getUser().getId(), boardId);
        return ResponseEntity.ok("모집 상태가 변경되었습니다.");
    }

    // 📌 작성자가 신청 승인
    @PutMapping("/{participationId}/approve")
    public ResponseEntity<String> approveParticipation(@PathVariable Long participationId,
                                                       @AuthenticationPrincipal PrincipalDetails principal) {
        boardService.approveParticipation(participationId, principal.getUser());
        return ResponseEntity.ok("참여가 승인되었습니다.");
    }

    // 📌 작성자가 신청 거절
    @PutMapping("/{participationId}/reject")
    public ResponseEntity<String> rejectParticipation(@PathVariable Long participationId,
                                                      @AuthenticationPrincipal PrincipalDetails principal) {
        boardService.rejectParticipation(participationId, principal.getUser());
        return ResponseEntity.ok("참여가 거절되었습니다.");
    }

}
