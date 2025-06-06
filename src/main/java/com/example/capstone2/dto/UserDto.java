package com.example.capstone2.dto;

import com.example.capstone2.domain.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private Long id;
    private String username;
    private String email;
    private String password;
    private Role role;
    private int credit;


    public UserDto(Long id, String name, String email, Role role, int credit) {
        this.id = id;
        this.username = name;
        this.email = email;
        this.role = role;
        this.credit = credit;
    }
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LoginReqDto {
        private String username;
        private String password;
    }
}
