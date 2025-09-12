package com.example.bookdahita.controller.client;

import com.example.bookdahita.models.*;
import com.example.bookdahita.repository.InventoryRepository;
import com.example.bookdahita.service.CategoryService;
import com.example.bookdahita.service.ProductService;
import com.example.bookdahita.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/client")
public class CatController {

    @Autowired
    private ProductService productService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private InventoryRepository inventoryRepository;

    @GetMapping("/category")
    public String category(@RequestParam("id") Long categoryId, @AuthenticationPrincipal CustomUserDetail userDetail, Model model) {
        // Lấy danh mục theo ID
        Category category = categoryService.findById(categoryId);
        if (category == null) {
            model.addAttribute("error", "Danh mục không tồn tại");
        }

        // Lấy danh sách sản phẩm theo categoryId
        List<Product> products = productService.getProductsByCategoryId(categoryId);

        // Lấy số lượng tồn kho cho các sản phẩm
        Map<Long, Integer> productInventoryMap = new HashMap<>();
        for (Product product : products) {
            Inventory inventory = inventoryRepository.findByProduct(product)
                    .orElse(Inventory.builder().quantity(0).build());
            productInventoryMap.put(product.getId(), inventory.getQuantity());
        }

        // Truyền dữ liệu vào Model
        model.addAttribute("category", category);
        model.addAttribute("products", products);
        model.addAttribute("listCategory", categoryService.getActiveCategoriesLimitedToTen());
        model.addAttribute("newProducts", productService.getNewActiveProductsLimitedToTen());
        model.addAttribute("productInventoryMap", productInventoryMap);

        // Thêm totalUniqueQuantity
        if (userDetail != null) {
            User user = userDetail.getUser();
            Transaction transaction = transactionService.getCartTransaction(user);
            int totalUniqueQuantity = (transaction != null && transaction.getOrders() != null) ?
                    (int) transaction.getOrders().stream()
                            .filter(order -> order != null && order.getProduct() != null)
                            .map(order -> order.getProduct().getId())
                            .distinct()
                            .count() : 0;
            model.addAttribute("totalUniqueQuantity", totalUniqueQuantity);
        } else {
            model.addAttribute("totalUniqueQuantity", 0);
        }

        return "client/category";
    }
}