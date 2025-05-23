package com.example.bookdahita.controller.client;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/client")
public class ProfileController {

    @GetMapping("/profile")
    public String profile(){
        return "client/profile";
    }
}
