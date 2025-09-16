package com.example.bookdahita.controller.client;

import com.example.bookdahita.models.User;
import com.example.bookdahita.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/client")
public class CustomLoginController {

    private static final Logger logger = LoggerFactory.getLogger(CustomLoginController.class);

    @Autowired
    private UserService userService;

    // Hiển thị trang đăng nhập
    @GetMapping("/login")
    public String showLoginForm(Model model, @RequestParam(value = "error", required = false) String error) {
        logger.info("Hiển thị form đăng nhập");
        model.addAttribute("user", new User());
        if (error != null) {
            model.addAttribute("error", "Email hoặc mật khẩu không đúng");
        }
        return "/client/login";
    }

    // Xử lý đăng nhập
    @PostMapping("/login")
    public String loginUser(@ModelAttribute User user, Model model, RedirectAttributes redirectAttributes) {
        try {
            logger.info("Xử lý đăng nhập cho email: {}", user.getEmail());
            boolean isAuthenticated = userService.authenticate(user.getEmail(), user.getPass());
            if (isAuthenticated) {
                logger.info("Đăng nhập thành công cho email: {}", user.getEmail());
                redirectAttributes.addFlashAttribute("success", "Đăng nhập thành công!");
                return "redirect:/client/home"; // Điều hướng đến trang chính
            } else {
                logger.warn("Đăng nhập thất bại cho email: {}", user.getEmail());
                model.addAttribute("error", "Email hoặc mật khẩu không đúng");
                model.addAttribute("user", user);
                return "/client/login";
            }
        } catch (Exception e) {
            logger.error("Lỗi khi đăng nhập cho email: {} - {}", user.getEmail(), e.getMessage());
            model.addAttribute("error", "Đã xảy ra lỗi: " + e.getMessage());
            model.addAttribute("user", user);
            return "/client/login";
        }
    }

    // Hiển thị trang đăng ký
    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        logger.info("Hiển thị form đăng ký");
        model.addAttribute("user", new User());
        return "/client/login";
    }

    // Xử lý đăng ký
    @PostMapping("/register")
    public String registerUser(@ModelAttribute User user, Model model, RedirectAttributes redirectAttributes) {
        try {
            logger.info("Xử lý đăng ký cho email: {}", user.getEmail());
            userService.registerNewUser(user);
            logger.info("Đăng ký thành công cho email: {}", user.getEmail());
            redirectAttributes.addFlashAttribute("success", "Đăng ký thành công! Vui lòng đăng nhập.");
            return "redirect:/client/login";
        } catch (Exception e) {
            logger.error("Lỗi khi đăng ký cho email: {} - {}", user.getEmail(), e.getMessage());
            model.addAttribute("error", e.getMessage());
            model.addAttribute("user", user);
            return "/client/login";
        }
    }
}