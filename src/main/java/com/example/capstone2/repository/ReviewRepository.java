package com.example.capstone2.repository;

import com.example.capstone2.domain.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    boolean existsByReviewerIdAndRevieweeId(Long reviewerId, Long revieweeId);
    List<Review> findByRevieweeId(Long revieweeId);
}