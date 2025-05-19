package com.example.bookdahita.controller.client;

import com.example.bookdahita.models.Category;
import com.example.bookdahita.models.Product;
import com.example.bookdahita.service.CategoryService;
import com.example.bookdahita.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/client")
public class CatController {

    @Autowired
    private ProductService productService;

    @Autowired
    private CategoryService categoryService;

    @GetMapping("/category")
    public String category(@RequestParam("id") Long categoryId, Model model) {
        // Lấy danh mục theo ID
        Category category = categoryService.findById(categoryId);
        if (category == null) {
            model.addAttribute("error", "Danh mục không tồn tại");
        }

        // Lấy danh sách sản phẩm theo categoryId
        List<Product> products = productService.getProductsByCategoryId(categoryId);

        // Truyền dữ liệu vào Model
        model.addAttribute("category", category);
        model.addAttribute("products", products);
        model.addAttribute("listCategory", categoryService.getActiveCategoriesLimitedToTen());
        model.addAttribute("newProducts", productService.getNewActiveProductsLimitedToTen());

        return "client/category";
    }
}