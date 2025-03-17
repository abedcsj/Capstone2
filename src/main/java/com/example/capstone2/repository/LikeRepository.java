package com.example.capstone2.repository;

import com.example.capstone2.domain.Likes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LikeRepository extends JpaRepository<Likes, Long> {
    // 특정 사용자와 특정 게시글에 대한 좋아요 조회
    Likes  findByUserIdAndBoardId(Long userId, Long boardId);

    // 특정 사용자와 특정 사용자 간의 좋아요 조
    Likes  findByUserIdAndLikedUserId(Long userId, Long likedUserId);
}
