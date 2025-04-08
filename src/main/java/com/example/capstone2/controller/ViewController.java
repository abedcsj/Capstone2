package com.example.capstone2.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ViewController {
    @GetMapping("/")
    public String redirectToRegister() {
        return "redirect:/register";
    }

    @GetMapping("/register")
    public String registerPage() {
        System.out.println("첫 화면 다이렉트");
        return "register"; // resources/templates/register.html
    }
}