package com.example.capstone2.service;

import com.example.capstone2.domain.Likes;
import com.example.capstone2.domain.User;
import com.example.capstone2.repository.LikeRepository;
import com.example.capstone2.repository.TradeHistoryRepository;
import com.example.capstone2.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LikeService {
    private final LikeRepository likeRepository;
    private final UserRepository userRepository;
    private final TradeHistoryRepository tradeHistoryRepository;

    @Transactional
    public void toggleLikeOnUser(Long userId, Long likedUserId) {
        // 거래 완료 유저 확인
        boolean hasTraded = tradeHistoryRepository.existsByUser1IdAndUser2IdAndIsCompleted(userId, likedUserId, true) ||
                tradeHistoryRepository.existsByUser1IdAndUser2IdAndIsCompleted(likedUserId, userId, true);

        if (!hasTraded) {
            throw new IllegalArgumentException("거래가 완료된 사용자만 좋아요를 남길 수 있습니다.");
        }

        // 좋아요 존재시 삭제
        Likes existingLike = likeRepository.findByUserIdAndLikedUserId(userId, likedUserId);
        if (existingLike != null) {
            likeRepository.delete(existingLike);
        } else {
            // 새로운 좋아요 저장
            User user = userRepository.findById(userId).orElseThrow();
            User likedUser = userRepository.findById(likedUserId).orElseThrow();

            Likes newLike = new Likes();
            newLike.setUser(user);
            newLike.setLikedUser(likedUser);
            likeRepository.save(newLike);
        }
    }
}
