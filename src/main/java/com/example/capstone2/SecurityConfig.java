package com.example.capstone2;

import com.example.capstone2.repository.UserRepository;
import com.example.capstone2.security.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@RequiredArgsConstructor
@EnableMethodSecurity
public class SecurityConfig {

    private final ObjectMapper objectMapper;
    private final AuthService authService;
    private final ExternalProperties externalProperties;
    private final UserRepository userRepository;


    @Bean
    public SecurityFilterChain securityFilterChain(
            org.springframework.security.config.annotation.web.builders.HttpSecurity http,
            AuthenticationManager authenticationManager
    ) throws Exception {

        JwtAuthenticationFilter jwtAuthenticationFilter = new JwtAuthenticationFilter(
                authenticationManager, objectMapper, authService, externalProperties
        );
        jwtAuthenticationFilter.setFilterProcessesUrl("/api/auth/login");

        return http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
//                .authorizeHttpRequests(auth -> auth
//                        // 인증 없이 접근 가능한 경로들
//                        .requestMatchers("/api/auth/**", "/api/token/**", "/api/users/register").permitAll()
//                        // 관리자 전용
//                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
//                        // 그 외 모든 요청은 USER 또는 ADMIN 권한만 있으면 접근 가능
//                        .anyRequest().hasAnyRole("USER", "ADMIN")
//                ) // 프론트 구현시 에러나서 원래 코드 주석처리
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/register", "/api/users/**", "/css/**", "/js/**").permitAll()
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        .anyRequest().hasAnyRole("USER", "ADMIN")
                )

                .addFilter(jwtAuthenticationFilter)
                .addFilter(new JwtAuthorizationFilter(authenticationManager, userRepository, authService, externalProperties))
                .build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
