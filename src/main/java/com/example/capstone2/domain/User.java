package com.example.capstone2.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String name;
    private String birthDate;
    private String phoneNumber;
    private String email;
    private String password;
    private int credit;

    // 📌사용자 역할 (USER or ADMIN)
    @Enumerated(EnumType.STRING)
    private Role role;

}
