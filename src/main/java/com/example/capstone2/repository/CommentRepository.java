package com.example.capstone2.repository;

import com.example.capstone2.domain.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByBoardId(Long boardId); // 게시글에 달린 댓글 조회용
}
