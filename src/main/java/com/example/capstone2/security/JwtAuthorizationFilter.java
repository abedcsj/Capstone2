package com.example.capstone2.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.capstone2.domain.User;
import com.example.capstone2.exception.NoMatchingDataException;
import com.example.capstone2.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import java.io.IOException;

public class JwtAuthorizationFilter extends BasicAuthenticationFilter {

    private final UserRepository userRepository;
    private final AuthService authService;
    private final ExternalProperties externalProperties;

    public JwtAuthorizationFilter(
            AuthenticationManager authenticationManager,
            UserRepository userRepository,
            AuthService authService,
            ExternalProperties externalProperties
    ) {
        super(authenticationManager);
        this.userRepository = userRepository;
        this.authService = authService;
        this.externalProperties = externalProperties;
    }

    /**
     * Access Token을 검증하고, 유효하면 Authentication을 생성해 SecurityContextHolder에 등록.
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        String jwtHeader = request.getHeader(externalProperties.getAccessKey());

        // JWT 헤더가 없거나 잘못된 형식이면 필터 통과시킴
        if (jwtHeader == null || !jwtHeader.startsWith(externalProperties.getTokenPrefix())) {
            chain.doFilter(request, response);
            return;
        }

        // "Bearer " 접두사 제거
        String tokenPrefix = externalProperties.getTokenPrefix();
        String accessToken = jwtHeader.substring(tokenPrefix.length());

        try {
            // JWT 검증 및 파싱
            DecodedJWT decodedJWT = JWT.require(authService.getTokenAlgorithm())
                    .build()
                    .verify(accessToken);

            Long userId = decodedJWT.getClaim("id").asLong();

            // DB에서 사용자 정보 조회
            User user = userRepository.findEntityGraphRoleTypeById(userId)
                    .orElseThrow(() -> new NoMatchingDataException("존재하지 않는 사용자 ID: " + userId));

            // PrincipalDetails 생성
            PrincipalDetails principalDetails = new PrincipalDetails(user);

            // 인증 객체 생성
            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    principalDetails,
                    null,
                    principalDetails.getAuthorities()
            );

            // SecurityContext에 등록
            SecurityContextHolder.getContext().setAuthentication(authentication);

        } catch (Exception e) {
            e.printStackTrace();
            SecurityContextHolder.clearContext();
        }
        chain.doFilter(request, response);
    }
}
