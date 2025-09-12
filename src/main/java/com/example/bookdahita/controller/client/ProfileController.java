package com.example.bookdahita.controller.client;

import com.example.bookdahita.models.User;
import com.example.bookdahita.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/client")
public class ProfileController {

    @Autowired
    private UserService userService;

    @GetMapping("/profile")
    public String profile(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userService.findByUserName(username);

        if (user != null) {
            model.addAttribute("user", user);
        } else {
            model.addAttribute("error", "Không tìm thấy thông tin người dùng");
        }

        return "client/profile";
    }

    @PostMapping("/profile")
    public String updateProfile(
            @RequestParam("name") String fullName,
            @RequestParam("email") String email,
            @RequestParam("phone") String phone,
            @RequestParam("address") String address,
            Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userService.findByUserName(username);

        if (user != null) {
            // Cập nhật thông tin người dùng
            user.setFullName(fullName);
            user.setEmail(email);
            user.setPhone(phone);
            user.setAddress(address);

            try {
                userService.updateUser(user); // Sử dụng updateUser thay vì registerNewUser
                model.addAttribute("success", "Cập nhật hồ sơ thành công!");
            } catch (Exception e) {
                model.addAttribute("error", "Lỗi khi cập nhật hồ sơ: " + e.getMessage());
            }
        } else {
            model.addAttribute("error", "Không tìm thấy thông tin người dùng");
        }

        model.addAttribute("user", user);
        return "client/profile";
    }
}