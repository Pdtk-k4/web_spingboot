package com.example.bookdahita.controller.client;

import com.example.bookdahita.models.User;
import com.example.bookdahita.service.UserService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Map;

@Controller
@RequestMapping("/client")
public class CustomLoginController {

    private static final Logger logger = LoggerFactory.getLogger(CustomLoginController.class);

    @Autowired
    private UserService userService;

    /**
     * Hiển thị form login/register
     */
    @GetMapping("/login")
    public String showLoginForm(@RequestParam(value = "error", required = false) String error,
                                @RequestParam(value = "logout", required = false) String logout,
                                Model model) {
        if (!model.containsAttribute("user")) {
            model.addAttribute("user", new User());
        }

        // Nếu Spring Security redirect với ?error
        if (error != null) {
            model.addAttribute("loginError", "Email hoặc mật khẩu không đúng");
        }

        // Nếu Spring Security redirect với ?logout
        if (logout != null) {
            model.addAttribute("success", "Bạn đã đăng xuất thành công");
        }

        // Lấy flash attributes từ model.asMap()
        Map<String, Object> attrs = model.asMap();
        String tab = (String) attrs.get("activeTab");
        Object loginError = attrs.get("loginError");
        Object registerError = attrs.get("registerError");
        Object success = attrs.get("success");

        logger.info("Login page -> tab={}, loginError={}, registerError={}, success={}",
                tab, loginError, registerError, success);

        model.addAttribute("tab", tab != null ? tab : "login");
        return "client/login";
    }

    /**
     * Trường hợp bạn muốn tự xử lý login (không đi qua Spring Security filter)
     * Nếu đã dùng Spring Security với .formLogin thì cái này có thể bỏ.
     */
    @PostMapping("/login")
    public String loginUser(@RequestParam("username") String email,
                            @RequestParam("password") String password,
                            RedirectAttributes redirectAttributes) {
        boolean isAuthenticated = userService.authenticate(email, password);

        if (isAuthenticated) {
            redirectAttributes.addFlashAttribute("success", "Đăng nhập thành công!");
            redirectAttributes.addFlashAttribute("activeTab", "login");
            return "redirect:/client/home";
        } else {
            redirectAttributes.addFlashAttribute("loginError", "Email hoặc mật khẩu không đúng");
            redirectAttributes.addFlashAttribute("activeTab", "login");
            logger.info("Đăng nhập thất bại -> loginError set");
            return "redirect:/client/login";
        }
    }

    /**
     * Chuyển qua tab đăng ký
     */
    @GetMapping("/register")
    public String showRegisterForm(RedirectAttributes redirectAttributes) {
        logger.info("Hiển thị form đăng ký");
        redirectAttributes.addFlashAttribute("activeTab", "register");
        return "redirect:/client/login";
    }

    /**
     * Xử lý đăng ký
     */
    @PostMapping("/register")
    public String registerUser(@ModelAttribute("user") @Valid User user,
                               BindingResult result,
                               RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.user", result);
            redirectAttributes.addFlashAttribute("user", user);
            redirectAttributes.addFlashAttribute("activeTab", "register");
            return "redirect:/client/login";
        }

        try {
            userService.registerNewUser(user);
            redirectAttributes.addFlashAttribute("success", "Đăng ký thành công! Vui lòng đăng nhập.");
            redirectAttributes.addFlashAttribute("activeTab", "login");
            return "redirect:/client/login";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.user", result);
            redirectAttributes.addFlashAttribute("user", user);
            redirectAttributes.addFlashAttribute("registerError", e.getMessage());
            redirectAttributes.addFlashAttribute("activeTab", "register");
            return "redirect:/client/login";
        }
    }

}
