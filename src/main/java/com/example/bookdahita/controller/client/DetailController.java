package com.example.bookdahita.controller.client;

import com.example.bookdahita.models.Product;
import com.example.bookdahita.models.ProductsImages;
import com.example.bookdahita.service.ProductImageService;
import com.example.bookdahita.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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

    @GetMapping("/detail")
    private String detail(@RequestParam("id") Long id, Model model) {
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
        return "client/detail";
    }

    @PostMapping("/add-to-cart")
    public String addToCart(@RequestParam("id") Long productId, Model model) {
        // Kiểm tra trạng thái đăng nhập
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getPrincipal().equals("anonymousUser")) {
            return "redirect:/client/login"; // Chuyển hướng đến trang đăng nhập
        }

        // Logic thêm sản phẩm vào giỏ hàng (giả định)
        // Ví dụ: productService.addToCart(productId, authentication.getName());
        return "redirect:/client/cart"; // Chuyển hướng đến trang giỏ hàng sau khi thêm
    }

    @PostMapping("/buy-now")
    public String buyNow(@RequestParam("id") Long productId, Model model) {
        // Kiểm tra trạng thái đăng nhập
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getPrincipal().equals("anonymousUser")) {
            return "redirect:/client/login"; // Chuyển hướng đến trang đăng nhập
        }

        // Logic mua ngay (giả định)
        // Ví dụ: productService.buyNow(productId, authentication.getName());
        return "redirect:/client/checkout"; // Chuyển hướng đến trang thanh toán
    }
}