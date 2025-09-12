package com.example.bookdahita.controller.client;

import com.example.bookdahita.models.User;
import com.example.bookdahita.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/client")
public class CustomLoginController {

    @Autowired
    private UserService userService;

    // Hiển thị trang đăng nhập
    @GetMapping("/login")
    public String showLoginForm(Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("error", model.containsAttribute("error") ? "Email hoặc mật khẩu không đúng" : null);
        return "/client/login";
    }

    // Hiển thị trang đăng ký
    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("user", new User());
        return "/client/login";
    }

    // Xử lý đăng ký
    @PostMapping("/register")
    public String registerUser(@ModelAttribute User user, Model model) {
        try {
            userService.registerNewUser(user);
            return "redirect:/client/login?success=true";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage()); // Hiển thị thông báo lỗi cụ thể
            model.addAttribute("user", user);
            return "/client/login";
        }
    }
}