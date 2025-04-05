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
    private final PrincipalDetailsService principalDetailsService;

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
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**", "/api/token/**").permitAll() // 로그인/토큰
                        .requestMatchers("/api/users/register").permitAll()            // 회원가입은 모두 허용!
                        .requestMatchers("/api/admin/**").hasAnyRole("ADMIN")        // 관리자 전용
                        .requestMatchers("/api/users/**").hasAnyRole("USER", "ADMIN")  // 그 외 유저용
                        .requestMatchers("/api/board-participation/**").hasAnyRole("USER", "ADMIN")
                        .anyRequest().authenticated()
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
