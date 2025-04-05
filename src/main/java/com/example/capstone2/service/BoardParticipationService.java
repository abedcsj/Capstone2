package com.example.capstone2.service;

import com.example.capstone2.domain.*;
import com.example.capstone2.repository.BoardParticipationRepository;
import com.example.capstone2.repository.BoardRepository;
import com.example.capstone2.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class BoardParticipationService {
    private final BoardParticipationRepository boardParticipationRepository;
    private final BoardRepository boardRepository;
    private final UserRepository userRepository;

    // 📌 게시글 참여 신청 (PENDING 상태 & 크레딧 결제)
    @Transactional
    public void requestParticipation(Long boardId, User user) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));
        User owner = board.getOwner();

        if (owner == null) {
            throw new IllegalStateException("게시글의 소유자가 존재하지 않습니다.");
        }

        if (board.getOwner().getId().equals(user.getId())) {
            throw new AccessDeniedException("자신의 게시글에는 참여 신청할 수 없습니다.");
        }

        int creditAmount = board.getCreditPrice();

        if (user.getCredit() < creditAmount) {
            throw new IllegalStateException("보유 크레딧이 부족합니다.");
        }

        // 참가자가 크레딧 차감
        user.setCredit(user.getCredit() - creditAmount);
        userRepository.save(user);

        // 참가 요청 생성
        BoardParticipation participation = new BoardParticipation();
        participation.setBoard(board);
        participation.setUser(user);
        participation.setStatus(ParticipationStatus.PENDING);
        participation.setRequestedAt(LocalDateTime.now());
        participation.setCreditAmount(creditAmount);

        boardParticipationRepository.save(participation);
    }

    // 📌 참가자가 환불 요청 (APPROVED → REFUNDED, 크레딧 반환)
    @Transactional
    public void requestRefund(Long participationId, User user) {
        BoardParticipation participation = boardParticipationRepository.findById(participationId)
                .orElseThrow(() -> new IllegalArgumentException("참여 정보를 찾을 수 없습니다."));

        if (!participation.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("본인만 환불 요청이 가능합니다.");
        }

        if (participation.getStatus() != ParticipationStatus.APPROVED) {
            throw new IllegalStateException("승인된 참가 요청만 환불할 수 있습니다.");
        }

        // 환불 상태 변경
        participation.setStatus(ParticipationStatus.REFUNDED);

        int refundedAmount = participation.getCreditAmount();
        user.setCredit(user.getCredit() + refundedAmount);
        userRepository.save(user);

        User owner = participation.getBoard().getOwner();
        owner.setCredit(owner.getCredit() - refundedAmount);
        userRepository.save(owner);

        boardParticipationRepository.save(participation);
    }
}
