package com.example.capstone2.controller;

import com.example.capstone2.domain.Board;
import com.example.capstone2.service.BoardService;
import com.example.capstone2.domain.Category;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/board")
public class BoardController {

    private final BoardService boardService;

    //📌카테고리별 게시판 조회 기능
    @GetMapping("/category/{category}")
    public ResponseEntity<List<Board>>getBoardsByCategory(@PathVariable Category category){
        return ResponseEntity.ok(boardService.getBoardsByCategory(category));
    }


}
