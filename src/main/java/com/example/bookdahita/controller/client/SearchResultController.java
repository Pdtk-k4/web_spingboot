package com.example.bookdahita.controller.client;

import com.example.bookdahita.models.Product;
import com.example.bookdahita.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/client")
public class SearchResultController {

    @Autowired
    private ProductRepository productRepository;

    @GetMapping("/searchresult")
    public String searchResult(@RequestParam("query") String query, Model model) {
        List<Product> products = productRepository.searchByNameOrCategory(query);
        model.addAttribute("products", products);
        model.addAttribute("query", query);
        return "client/searchresult";
    }
}
