package com.example.capstone2.repository;

import com.example.capstone2.domain.Board;
import com.example.capstone2.domain.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BoardRepository extends JpaRepository<Board, Long> {
    List<Board>findByCategory(Category category);
    // 제목에 특정 키워드가 포함된 게시글 찾기 (대소문자 구분 없이 포함 검색)
    List<Board> findByTitleContainingIgnoreCase(String keyword);

}
