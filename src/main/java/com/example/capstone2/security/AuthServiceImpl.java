package com.example.capstone2.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.example.capstone2.domain.RefreshToken;
import com.example.capstone2.domain.User;
import com.example.capstone2.exception.InvalidTokenException;
import com.example.capstone2.repository.RefreshTokenRepository;
import com.example.capstone2.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class AuthServiceImpl implements AuthService {

    private final ExternalProperties externalProperties;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;

    public AuthServiceImpl(
            ExternalProperties externalProperties,
            RefreshTokenRepository refreshTokenRepository,
            UserRepository userRepository
    ) {
        this.externalProperties = externalProperties;
        this.refreshTokenRepository = refreshTokenRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Algorithm getTokenAlgorithm() {
        return Algorithm.HMAC512(externalProperties.getTokenSecretKey());
    }

    /**
     * Access Token 생성 - userId와 role 포함
     */
    @Override
    public String createAccessToken(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));

        return JWT.create()
                .withSubject("accessToken")
                .withClaim("id", userId)
                .withClaim("role", user.getRole().name()) // ROLE claim 추가
                .withExpiresAt(new Date(System.currentTimeMillis() + externalProperties.getAccessTokenExpirationTime()))
                .sign(getTokenAlgorithm());
    }

    /**
     * Access Token 검증
     */
    @Override
    public Long verifyAccessToken(String accessToken) throws JWTVerificationException {
        return JWT.require(getTokenAlgorithm())
                .build()
                .verify(accessToken)
                .getClaim("id").asLong();
    }

    /**
     * Refresh Token 생성 - userId 포함, DB 저장
     */
    @Override
    public String createRefreshToken(Long userId) {
        revokeRefreshToken(userId);

        String refreshToken = JWT.create()
                .withSubject("refreshToken")
                .withClaim("id", userId)
                .withExpiresAt(new Date(System.currentTimeMillis() + externalProperties.getRefreshTokenExpirationTime()))
                .sign(getTokenAlgorithm());

        refreshTokenRepository.save(RefreshToken.of(refreshToken, userId));
        return refreshToken;
    }

    /**
     * Refresh Token 삭제
     */
    @Override
    public void revokeRefreshToken(Long userId) {
        refreshTokenRepository.deleteAll(refreshTokenRepository.findByUserId(userId));
    }

    /**
     * Refresh Token 검증
     */
    @Override
    public Long verifyRefreshToken(String refreshToken) throws JWTVerificationException {
        refreshTokenRepository.findByContent(refreshToken)
                .orElseThrow(() -> new InvalidTokenException("Refresh token is invalid or expired"));

        return JWT.require(getTokenAlgorithm())
                .build()
                .verify(refreshToken)
                .getClaim("id").asLong();
    }

    /**
     * Refresh Token을 통해 Access Token 발급
     */
    @Override
    public String issueAccessToken(String refreshToken) throws JWTVerificationException {
        Long userId = verifyRefreshToken(refreshToken);
        return createAccessToken(userId);
    }

    @Override
    public String extractUserRole(String accessToken) {
        return JWT.require(getTokenAlgorithm())
                .build()
                .verify(accessToken)
                .getClaim("role").asString();  // JWT에 들어간 "role" 클레임 꺼냄
    }

}