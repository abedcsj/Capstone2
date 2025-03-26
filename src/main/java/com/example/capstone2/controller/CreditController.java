package com.example.capstone2.controller;

import com.example.capstone2.domain.CreditStatus;
import com.example.capstone2.domain.User;
import com.example.capstone2.dto.CreditDto;
import com.example.capstone2.service.CreditService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/credits")
@RequiredArgsConstructor
public class CreditController {
    private final CreditService creditService;

    // 📌 크레딧 생성 API (인증된 사용자 기반)
    @PostMapping
    public ResponseEntity<CreditDto> createCredit(@RequestBody CreditDto creditDTO,
                                                  @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(creditService.createCredit(creditDTO, user));
    }

    // 📌 본인이 보낸 크레딧 목록 조회 API
    @GetMapping("/from/{userId}")
    public ResponseEntity<List<CreditDto>> getCreditsByFromUser(@PathVariable Long userId,
                                                                @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(creditService.getCreditsByFromUser(user, userId));
    }

    // 📌 본인이 받은 크레딧 목록 조회 API
    @GetMapping("/to/{userId}")
    public ResponseEntity<List<CreditDto>> getCreditsByToUser(@PathVariable Long userId,
                                                              @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(creditService.getCreditsByToUser(user, userId));
    }

    // 📌 크레딧 상태 변경 API (작성자만 변경 가능)
    @PutMapping("/{creditId}/status")
    public ResponseEntity<CreditDto> updateCreditStatus(@PathVariable Long creditId,
                                                        @RequestParam CreditStatus status,
                                                        @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(creditService.updateCreditStatus(creditId, status, user));
    }
}
