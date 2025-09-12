package com.example.bookdahita.controller.client;

import com.example.bookdahita.models.Product;
import com.example.bookdahita.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/client")
public class SearchController {

    @Autowired
    private ProductRepository productRepository;

    @GetMapping("/search")
    public Map<String, Object> searchProducts(@RequestParam("query") String query) {
        System.out.println("Tìm kiếm với từ khóa: " + query); // Debug
        List<Product> products = productRepository.searchByNameOrCategory(query)
                .stream()
                .limit(5)
                .collect(Collectors.toList());
        System.out.println("Số kết quả tìm kiếm: " + products.size()); // Debug
        Map<String, Object> response = new HashMap<>();
        response.put("results", products);
        response.put("total", products.size());
        return response;
    }
}