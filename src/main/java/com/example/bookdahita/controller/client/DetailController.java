package com.example.bookdahita.controller.client;

import com.example.bookdahita.models.*;
import com.example.bookdahita.service.ProductImageService;
import com.example.bookdahita.service.ProductService;
import com.example.bookdahita.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/client")
public class DetailController {

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductImageService productImageService;

    @Autowired
    private TransactionService transactionService;

    @GetMapping("/detail")
    private String detail(@RequestParam("id") Long id, @AuthenticationPrincipal CustomUserDetail userDetail, Model model) {
        Product product = productService.findById(id);
        if (product == null) {
            return "redirect:/client/error"; // Chuyển hướng nếu không tìm thấy sản phẩm
        }
        List<ProductsImages> productImages = productImageService.getAll().stream()
                .filter(img -> img.getProduct() != null && img.getProduct().getId().equals(id))
                .toList();
        List<Product> featuredProducts = productService.getActiveProducts();
        List<Product> relatedProducts = productService.getProductsByCategoryId(product.getCategory().getId())
                .stream()
                .filter(p -> !p.getId().equals(id)) // Loại bỏ sản phẩm hiện tại khỏi danh sách liên quan
                .limit(5) // Giới hạn tối đa 5 sản phẩm liên quan
                .toList();

        model.addAttribute("product", product);
        model.addAttribute("productImages", productImages);
        model.addAttribute("featuredProducts", featuredProducts);
        model.addAttribute("relatedProducts", relatedProducts);

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

        return "client/detail";
    }

    @PostMapping("/add-to-cart")
    public String addToCart(@RequestParam("id") Long productId, @AuthenticationPrincipal CustomUserDetail userDetail, Model model) {
        // Kiểm tra trạng thái đăng nhập
        if (userDetail == null) {
            return "redirect:/client/login"; // Chuyển hướng đến trang đăng nhập
        }

        // Logic thêm sản phẩm vào giỏ hàng (giả định)
        try {
            User user = userDetail.getUser();
            transactionService.addProductToTransaction(user, productService.findById(productId), 1);
            return "redirect:/client/cart";
        } catch (Exception e) {
            model.addAttribute("error", "Lỗi khi thêm sản phẩm: " + e.getMessage());
            return "redirect:/client/detail?id=" + productId;
        }
    }

    @PostMapping("/buy-now")
    public String buyNow(@RequestParam("id") Long productId, @AuthenticationPrincipal CustomUserDetail userDetail, Model model) {
        // Kiểm tra trạng thái đăng nhập
        if (userDetail == null) {
            return "redirect:/client/login"; // Chuyển hướng đến trang đăng nhập
        }

        // Logic mua ngay (giả định)
        try {
            User user = userDetail.getUser();
            transactionService.addProductToTransaction(user, productService.findById(productId), 1);
            return "redirect:/client/checkout";
        } catch (Exception e) {
            model.addAttribute("error", "Lỗi khi mua ngay: " + e.getMessage());
            return "redirect:/client/detail?id=" + productId;
        }
    }
}