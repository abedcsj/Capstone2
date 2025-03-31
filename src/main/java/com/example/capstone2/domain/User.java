package com.example.capstone2.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // AUTO → IDENTITY가 일반적
    private Long id;

    private String username;
    private String email;
    private String password;
    private int credit;

    @Enumerated(EnumType.STRING)
    private Role role; // USER or ADMIN
}
