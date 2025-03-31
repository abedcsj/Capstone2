package com.example.capstone2.service;

import com.example.capstone2.domain.Board;
import com.example.capstone2.domain.Credit;
import com.example.capstone2.domain.CreditStatus;
import com.example.capstone2.domain.CreditType;
import com.example.capstone2.domain.User;
import com.example.capstone2.dto.CreditDto;
import com.example.capstone2.repository.BoardRepository;
import com.example.capstone2.repository.CreditRepository;
import com.example.capstone2.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class CreditService {
    private final CreditRepository creditRepository;
    private final UserRepository userRepository;
    private final BoardRepository boardRepository;

    // 📌 사용자 본인이 보낸 크레딧 내역 조회 (보안 검증 포함)
    public List<CreditDto> getCreditsByFromUser(User authenticatedUser, Long fromUserId) {
        if (!authenticatedUser.getId().equals(fromUserId)) {
            throw new RuntimeException("자신의 보낸 크레딧 내역만 조회할 수 있습니다.");
        }
        return creditRepository.findByFromUserId(fromUserId).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    // 📌 사용자 본인이 받은 크레딧 내역 조회 (보안 검증 포함)
    public List<CreditDto> getCreditsByToUser(User authenticatedUser, Long toUserId) {
        if (!authenticatedUser.getId().equals(toUserId)) {
            throw new RuntimeException("자신의 받은 크레딧 내역만 조회할 수 있습니다.");
        }
        return creditRepository.findByToUserId(toUserId).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * 📌 크레딧 요청 생성 (보안 고려)
     * 인증된 사용자(authenticatedUser)가 직접 호출한 경우에만 유효한 요청으로 처리
     */
    @Transactional
    public CreditDto createCredit(CreditDto creditDTO, User authenticatedUser) {
        if (!authenticatedUser.getId().equals(creditDTO.getFromUserId())) {
            throw new RuntimeException("인증된 사용자와 요청된 보내는 사용자 정보가 일치하지 않습니다.");
        }

        Credit credit = new Credit();
        credit.setFromUser(authenticatedUser);
        credit.setToUser(userRepository.findById(creditDTO.getToUserId())
                .orElseThrow(() -> new RuntimeException("받는 사용자를 찾을 수 없음")));
        credit.setBoard(boardRepository.findById(creditDTO.getBoardId())
                .orElseThrow(() -> new RuntimeException("게시판을 찾을 수 없음")));
        credit.setAmount(creditDTO.getAmount());
        credit.setType(creditDTO.getType());
        credit.setStatus(CreditStatus.PENDING);
        credit.setTransactionTime(LocalDateTime.now());

        credit = creditRepository.save(credit);
        return convertToDto(credit);
    }

    // 📌 크레딧 상태 변경 (APPROVED / REFUNDED) - 게시글 작성자만 가능
    @Transactional
    public CreditDto updateCreditStatus(Long creditId, CreditStatus status, User authenticatedUser) {
        Credit credit = creditRepository.findById(creditId)
                .orElseThrow(() -> new RuntimeException("해당 ID의 크레딧이 존재하지 않음"));

        if (!credit.getBoard().getOwner().getId().equals(authenticatedUser.getId())) {
            throw new RuntimeException("해당 게시글의 작성자만 상태를 변경할 수 있습니다.");
        }

        if (status == CreditStatus.APPROVED) {
            User sender = credit.getFromUser();
            User receiver = credit.getToUser();

            if (sender.getCredit() < credit.getAmount()) {
                throw new RuntimeException("보낸 사용자의 크레딧이 부족합니다.");
            }

            sender.setCredit(sender.getCredit() - credit.getAmount());
            receiver.setCredit(receiver.getCredit() + credit.getAmount());

            userRepository.save(sender);
            userRepository.save(receiver);
        }

        if (status == CreditStatus.REFUNDED) {
            User sender = credit.getFromUser();
            sender.setCredit(sender.getCredit() + credit.getAmount());
            userRepository.save(sender);
        }

        credit.setStatus(status);
        credit = creditRepository.save(credit);
        return convertToDto(credit);
    }

    // 📌 사용자 전체 크레딧 내역 조회 (보낸 & 받은) - 보안 적용
    public List<CreditDto> getUserCredits(User authenticatedUser) {
        Long userId = authenticatedUser.getId();
        List<Credit> sentCredits = creditRepository.findByFromUserId(userId);
        List<Credit> receivedCredits = creditRepository.findByToUserId(userId);

        return Stream.concat(sentCredits.stream(), receivedCredits.stream())
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    // 📌 관리자 권한으로 특정 사용자의 크레딧 내역 조회
    public List<CreditDto> getUserCreditsByUserId(Long userId) {
        List<Credit> sentCredits = creditRepository.findByFromUserId(userId);
        List<Credit> receivedCredits = creditRepository.findByToUserId(userId);

        return Stream.concat(sentCredits.stream(), receivedCredits.stream())
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    // 📌 엔티티 -> DTO 변환
    private CreditDto convertToDto(Credit credit) {
        return new CreditDto(
                credit.getId(),
                credit.getFromUser().getId(),
                credit.getToUser().getId(),
                credit.getBoard().getId(),
                credit.getAmount(),
                credit.getType(),
                credit.getStatus(),
                credit.getTransactionTime()
        );
    }
}