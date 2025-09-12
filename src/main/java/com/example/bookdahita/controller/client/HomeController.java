package com.example.bookdahita.controller.client;

import com.example.bookdahita.models.*;
import com.example.bookdahita.repository.InventoryRepository;
import com.example.bookdahita.service.CategoryService;
import com.example.bookdahita.service.ProductService;
import com.example.bookdahita.service.SlideService;
import com.example.bookdahita.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class HomeController {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private ProductService productService;

    @Autowired
    private SlideService slideService;

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private InventoryRepository inventoryRepository;

    @RequestMapping("/DahitaBook")
    public String home(@AuthenticationPrincipal CustomUserDetail userDetail, Model model) {
        // Lấy danh mục nổi bật (tối đa 4, catstatus = 1)
        List<Category> featuredCategories = categoryService.getActiveCategoriesLimitedToFour();

        // Tạo map để lưu danh mục và danh sách sản phẩm tương ứng
        Map<Category, List<Product>> categoryProductsMap = new HashMap<>();
        Map<Long, Integer> productInventoryMap = new HashMap<>();
        for (Category category : featuredCategories) {
            List<Product> products = productService.getProductsByCategoryId(category.getId())
                    .stream()
                    .filter(p -> p.getProstatus() != null && p.getProstatus() && p.getProsale() != null && p.getProsale() > 0)
                    .limit(8)
                    .collect(Collectors.toList());
            categoryProductsMap.put(category, products);
            // Lấy số lượng tồn kho cho các sản phẩm
            for (Product product : products) {
                Inventory inventory = inventoryRepository.findByProduct(product)
                        .orElse(Inventory.builder().quantity(0).build());
                productInventoryMap.put(product.getId(), inventory.getQuantity());
            }
        }

        // Lấy danh sách sản phẩm mới
        List<Product> newProducts = productService.getNewActiveProductsLimitedToTen();
        for (Product product : newProducts) {
            Inventory inventory = inventoryRepository.findByProduct(product)
                    .orElse(Inventory.builder().quantity(0).build());
            productInventoryMap.put(product.getId(), inventory.getQuantity());
        }

        // Lấy các slide hoạt động (slactive = true, tối đa 4)
        List<Slide> activeSlides = slideService.getActiveSlidesLimitedToFour();

        // Thêm vào model
        model.addAttribute("listCategory", categoryService.getActiveCategoriesLimitedToTen());
        model.addAttribute("newProducts", newProducts);
        model.addAttribute("featuredCategories", featuredCategories);
        model.addAttribute("categoryProductsMap", categoryProductsMap);
        model.addAttribute("activeSlides", activeSlides);
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

        return "client/index";
    }
}