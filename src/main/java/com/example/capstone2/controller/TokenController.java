package com.example.capstone2.controller;

import com.example.capstone2.security.AuthService;
import com.example.capstone2.security.ExternalProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/token")
@RequiredArgsConstructor
public class TokenController {

    private final AuthService authService;
    private final ExternalProperties externalProperties;

    /**
     * RefreshToken 기반 AccessToken 재발급 API
     * 클라이언트는 헤더에 RefreshToken을 넣어서 요청해야 함
     */
    @PostMapping("/refresh")
    public ResponseEntity<?> refreshAccessToken(@RequestHeader("Refresh") String refreshToken) {
        // "Bearer " 접두사 제거
        String pureToken = refreshToken.replace(externalProperties.getTokenPrefix(), "");

        // AccessToken 재발급
        String newAccessToken = authService.issueAccessToken(pureToken);

        // 응답 형태: Header + Body 둘 다 가능하게
        return ResponseEntity.ok()
                .header(externalProperties.getAccessKey(), externalProperties.getTokenPrefix() + newAccessToken)
                .body(Map.of(
                        "accessToken", externalProperties.getTokenPrefix() + newAccessToken
                ));
    }
}
