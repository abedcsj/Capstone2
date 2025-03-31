package com.example.capstone2.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

/*
 */
@Getter
@Entity
public class RefreshToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter String content;
    @Setter Long userId;


    protected RefreshToken(){}
    private RefreshToken(String content, Long userId) {

        this.content = content;
        this.userId = userId;
    }
    public static RefreshToken of(String content, Long userId) {
        return new RefreshToken(content, userId);
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }
}