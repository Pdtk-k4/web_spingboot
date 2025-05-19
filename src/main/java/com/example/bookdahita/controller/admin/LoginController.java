package com.example.bookdahita.controller.admin;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class LoginController {
    @GetMapping("/admin/login")
    public String login() {
        return "admin/login"; // Loại bỏ dấu "/" ở đầu để phù hợp với Thymeleaf
    }
}