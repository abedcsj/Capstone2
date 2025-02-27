package com.example.capstone2.controller;


import com.example.capstone2.domain.Category;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/category")
public class CategoryController {

    // 📌카테고리 리스트를 조회 API-성준
    @GetMapping
    public ResponseEntity<Category[]>getCategories(){
        return ResponseEntity.ok(Category.values());
    }
}
