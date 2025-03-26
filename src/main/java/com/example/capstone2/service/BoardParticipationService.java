package com.example.capstone2.service;

import com.example.capstone2.domain.*;
import com.example.capstone2.dto.BoardParticipationDto;
import com.example.capstone2.repository.BoardParticipationRepository;
import com.example.capstone2.repository.BoardRepository;
import com.example.capstone2.repository.UserRepository;
import com.example.capstone2.repository.CreditRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BoardParticipationService {
    private final BoardParticipationRepository boardParticipationRepository;
    private final BoardRepository boardRepository;
    private final CreditRepository creditRepository;

    // 📌 게시글 참여 신청 (PENDING 상태 & 크레딧 결제)
    @Transactional
    public void requestParticipation(Long boardId, User user) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));
        User owner = board.getOwner();

        if (owner == null) {
            throw new IllegalStateException("게시글의 소유자가 존재하지 않습니다.");
        }

        // 참가 요청 생성
        BoardParticipation participation = new BoardParticipation();
        participation.setBoard(board);
        participation.setUser(user);
        participation.setStatus(ParticipationStatus.PENDING);
        participation.setRequestedAt(LocalDateTime.now());

        int creditAmount = board.getCreditPrice();

        // 📌 참가자가 크레딧을 보냄 (SEND)
        Credit sendCredit = new Credit();
        sendCredit.setFromUser(user);
        sendCredit.setToUser(owner);
        sendCredit.setAmount(creditAmount);
        sendCredit.setType(CreditType.SEND);
        sendCredit.setStatus(CreditStatus.PENDING);
        sendCredit.setTransactionTime(LocalDateTime.now());

        creditRepository.save(sendCredit);

        // 참가 신청과 크레딧 연결
        participation.setCredit(sendCredit);
        boardParticipationRepository.save(participation);
    }

    // 📌 참가자가 환불 요청 (APPROVED → REFUNDED, 크레딧 반환)
    @Transactional
    public void requestRefund(Long participationId, User user) {
        BoardParticipation participation = boardParticipationRepository.findById(participationId)
                .orElseThrow(() -> new IllegalArgumentException("참여 정보를 찾을 수 없습니다."));

        if (!participation.getUser().getId().equals(user.getId())) {
            throw new IllegalStateException("본인만 환불 요청이 가능합니다.");
        }

        if (participation.getStatus() != ParticipationStatus.APPROVED) {
            throw new IllegalStateException("승인된 참가 요청만 환불할 수 있습니다.");
        }

        // 환불 상태 변경
        participation.setStatus(ParticipationStatus.REFUNDED);

        Credit credit = participation.getCredit();
        credit.setStatus(CreditStatus.REFUNDED);

        User sender = credit.getFromUser();
        sender.setCredit(sender.getCredit() + credit.getAmount());

        creditRepository.save(credit);
        boardParticipationRepository.save(participation);
    }

}
