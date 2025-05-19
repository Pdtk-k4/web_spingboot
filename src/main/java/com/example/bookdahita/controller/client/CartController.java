package com.example.bookdahita.controller.client;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/client")
public class CartController {

    @GetMapping("/cart")
    public String showCart(Model model) {
        return "client/cart";
    }
}
