package com.example.capstone2.security;

import com.auth0.jwt.JWT;
import com.example.capstone2.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import java.io.IOException;
import java.util.List;

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

        if (jwtHeader == null || !jwtHeader.startsWith(externalProperties.getTokenPrefix())) {
            chain.doFilter(request, response);
            return;
        }

        String tokenPrefix = externalProperties.getTokenPrefix();
        String accessToken = jwtHeader.substring(tokenPrefix.length());

        try {
            // ⬇️ userId + role 디코딩
            Long userId = JWT.require(authService.getTokenAlgorithm())
                    .build()
                    .verify(accessToken)
                    .getClaim("id").asLong();

            String role = JWT.require(authService.getTokenAlgorithm())
                    .build()
                    .verify(accessToken)
                    .getClaim("role").asString();

            // ⬇️ Spring 권한 객체 생성
            SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + role);
            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    userId, // 또는 PrincipalDetails(user)
                    null,
                    List.of(authority)
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

        } catch (Exception e) {
            SecurityContextHolder.clearContext();
        }

        chain.doFilter(request, response);
    }
}