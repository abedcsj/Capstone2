package com.example.capstone2.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

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

}