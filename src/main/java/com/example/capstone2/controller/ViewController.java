package com.example.capstone2.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class ViewController {

    @GetMapping("/")
    public String redirectToLogin() {
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String loginPage() {
        System.out.println("첫 화면 다이렉트");
        return "login"; // resources/templates/login.html
    }

    @GetMapping("/register")
    public String showRegisterPage() {
        return "register"; // templates/register.html
    }

    @GetMapping("/mypage") // ✅ 로그인 후 보여줄 내 정보 페이지
    public String myPage() {
        return "mypage"; // templates/mypage.html
    }

    @GetMapping("/boardpage")
    public String boardPage() { return "boardpage"; }

    @GetMapping("/board/create")
    public String createBoardPage() {
        System.out.println("🔥 create 페이지 호출됨");
        return "createboard"; // templates/createboard.html
    }

    @GetMapping("/board/modify/{boardId}")
    public String modifyBoardPage(@PathVariable Long boardId) {
        System.out.println("✏️ modify 페이지 호출됨 - boardId = " + boardId);
        return "modifyboardpage"; // templates/modifyboardpage.html
    }

    @GetMapping("/board/show/{id}")
    public String showBoardPage(@PathVariable Long id) {
        return "boardshow"; // boardshow.html
    }
}