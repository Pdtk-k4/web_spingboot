package com.example.bookdahita.controller.client;

import com.example.bookdahita.models.CustomUserDetail;
import com.example.bookdahita.models.Transaction;
import com.example.bookdahita.models.User;
import com.example.bookdahita.service.ProductService;
import com.example.bookdahita.service.TransactionService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

@Controller
@RequestMapping("/client")
public class CartController {

    private static final Logger logger = Logger.getLogger(CartController.class.getName());

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private ProductService productService;

    @GetMapping("/cart/count")
    public ResponseEntity<Map<String, Integer>> getCartCount(@AuthenticationPrincipal CustomUserDetail userDetail) {
        Map<String, Integer> response = new HashMap<>();
        if (userDetail == null) {
            response.put("count", 0);
            return ResponseEntity.ok(response);
        }
        try {
            User user = userDetail.getUser();
            Transaction transaction = transactionService.getCartTransaction(user);
            int distinctProductCount = (transaction != null && transaction.getOrders() != null) ?
                    (int) transaction.getOrders().stream()
                            .filter(order -> order != null && order.getProduct() != null)
                            .map(order -> order.getProduct().getId())
                            .distinct()
                            .count() : 0;
            response.put("count", distinctProductCount);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.severe("Error in getCartCount: " + e.getMessage());
            response.put("count", 0);
            return ResponseEntity.ok(response);
        }
    }

    @GetMapping("/cart/total-unique-quantity")
    public ResponseEntity<Map<String, Integer>> getTotalUniqueQuantity(@AuthenticationPrincipal CustomUserDetail userDetail) {
        Map<String, Integer> response = new HashMap<>();
        if (userDetail == null) {
            response.put("totalUniqueQuantity", 0);
            return ResponseEntity.ok(response);
        }
        try {
            User user = userDetail.getUser();
            Transaction transaction = transactionService.getCartTransaction(user);
            int totalUniqueQuantity = (transaction != null && transaction.getOrders() != null) ?
                    (int) transaction.getOrders().stream()
                            .filter(order -> order != null && order.getProduct() != null)
                            .map(order -> order.getProduct().getId())
                            .distinct()
                            .count() : 0;
            response.put("totalUniqueQuantity", totalUniqueQuantity);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.severe("Error in getTotalUniqueQuantity: " + e.getMessage());
            response.put("totalUniqueQuantity", 0);
            return ResponseEntity.ok(response);
        }
    }

    @GetMapping("/cart")
    public String showCart(@AuthenticationPrincipal CustomUserDetail userDetail, Model model) {
        logger.info("Accessing /client/cart");
        if (userDetail == null) {
            logger.warning("User not authenticated, redirecting to login");
            return "redirect:/client/login";
        }
        try {
            User user = userDetail.getUser();
            Transaction transaction;
            try {
                transaction = transactionService.getCartTransaction(user);
            } catch (Exception e) {
                logger.severe("Error fetching transaction: " + e.getMessage());
                model.addAttribute("transaction", null);
                model.addAttribute("totalPrice", 0);
                model.addAttribute("totalUniqueQuantity", 0);
                return "client/cart";
            }
            if (transaction == null || transaction.getOrders() == null || transaction.getOrders().isEmpty()) {
                if (transaction != null) {
                    transactionService.deleteTransactionIfEmpty(transaction);
                }
                model.addAttribute("transaction", null);
                model.addAttribute("totalPrice", 0);
                model.addAttribute("totalUniqueQuantity", 0);
            } else {
                int totalPrice = transaction.getOrders().stream()
                        .mapToInt(order -> {
                            if (order == null || order.getPrice() == null || order.getQuantity() == null) return 0;
                            return order.getPrice() * order.getQuantity(); // Giá đã giảm từ TransactionService
                        })
                        .sum();
                int totalUniqueQuantity = (int) transaction.getOrders().stream()
                        .filter(order -> order != null && order.getProduct() != null)
                        .map(order -> order.getProduct().getId())
                        .distinct()
                        .count();
                model.addAttribute("transaction", transaction);
                model.addAttribute("totalPrice", totalPrice);
                model.addAttribute("totalUniqueQuantity", totalUniqueQuantity);
            }
            return "client/cart";
        } catch (Exception e) {
            logger.severe("Error in showCart: " + e.getMessage());
            model.addAttribute("error", "Không tìm thấy giỏ hàng: " + e.getMessage());
            model.addAttribute("transaction", null);
            model.addAttribute("totalPrice", 0);
            model.addAttribute("totalUniqueQuantity", 0);
            return "client/cart";
        }
    }

    @PostMapping("/cart/add/{productId}")
    public ResponseEntity<Map<String, String>> addToCart(
            @AuthenticationPrincipal CustomUserDetail userDetail,
            @PathVariable Long productId,
            @RequestParam(value = "quantity", defaultValue = "1") int quantity,
            HttpSession session
    ) {
        Map<String, String> response = new HashMap<>();
        logger.info("Add to cart: productId=" + productId + ", quantity=" + quantity);
        if (userDetail == null) {
            session.setAttribute("pendingProductId", productId);
            session.setAttribute("pendingQuantity", quantity);
            response.put("status", "error");
            response.put("message", "Vui lòng đăng nhập để thêm sản phẩm vào giỏ hàng");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
        try {
            User user = userDetail.getUser();
            transactionService.addProductToTransaction(user, productService.findById(productId), quantity);
            response.put("status", "success");
            response.put("message", "Sản phẩm đã được thêm vào giỏ hàng");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.severe("Error adding product to cart: " + e.getMessage());
            response.put("status", "error");
            response.put("message", "Lỗi khi thêm sản phẩm: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping("/cart/update/{productId}")
    public ResponseEntity<Map<String, Object>> updateCartItemQuantity(
            @AuthenticationPrincipal CustomUserDetail userDetail,
            @PathVariable Long productId,
            @RequestParam("quantity") int quantity,
            @RequestParam("transactionId") Long transactionId
    ) {
        Map<String, Object> response = new HashMap<>();
        if (userDetail == null) {
            response.put("status", "error");
            response.put("message", "Vui lòng đăng nhập để cập nhật giỏ hàng");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
        try {
            User user = userDetail.getUser();
            Transaction transaction = transactionService.getCartTransaction(user);
            if (transaction == null || transaction.getOrders() == null || transaction.getOrders().isEmpty()) {
                response.put("status", "error");
                response.put("message", "Giỏ hàng không tồn tại hoặc trống");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
            if (!transaction.getId().equals(transactionId)) {
                throw new IllegalArgumentException("Transaction ID không hợp lệ");
            }
            transactionService.updateOrderQuantity(transaction, productId, quantity);
            int totalPrice = transaction.getOrders().stream()
                    .mapToInt(order -> {
                        if (order == null || order.getPrice() == null || order.getQuantity() == null) return 0;
                        return order.getPrice() * order.getQuantity(); // Giá đã giảm
                    })
                    .sum();
            int lineItemTotal = transaction.getOrders().stream()
                    .filter(order -> order != null && order.getProduct() != null && order.getProduct().getId().equals(productId))
                    .findFirst()
                    .map(order -> order.getPrice() * quantity) // Giá đã giảm
                    .orElse(0);
            response.put("status", "success");
            response.put("message", "Cập nhật số lượng thành công");
            response.put("quantity", quantity);
            response.put("lineItemTotal", lineItemTotal);
            response.put("totalPrice", totalPrice);
            response.put("totalUniqueQuantity", (int) transaction.getOrders().stream()
                    .filter(order -> order != null && order.getProduct() != null)
                    .map(order -> order.getProduct().getId())
                    .distinct()
                    .count());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.severe("Error updating cart item quantity: " + e.getMessage());
            response.put("status", "error");
            response.put("message", "Lỗi khi cập nhật số lượng: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping("/cart/remove/{productId}")
    public ResponseEntity<Map<String, Object>> removeCartItem(
            @AuthenticationPrincipal CustomUserDetail userDetail,
            @PathVariable Long productId,
            @RequestParam("transactionId") Long transactionId
    ) {
        Map<String, Object> response = new HashMap<>();
        if (userDetail == null) {
            response.put("status", "error");
            response.put("message", "Vui lòng đăng nhập để xóa sản phẩm");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
        try {
            User user = userDetail.getUser();
            Transaction transaction = transactionService.getCartTransaction(user);
            if (transaction == null || transaction.getOrders() == null || transaction.getOrders().isEmpty()) {
                response.put("status", "error");
                response.put("message", "Giỏ hàng không tồn tại hoặc trống");
                response.put("isCartEmpty", true);
                response.put("totalPrice", 0);
                response.put("cartCount", 0);
                response.put("totalUniqueQuantity", 0);
                return ResponseEntity.ok(response);
            }
            if (!transaction.getId().equals(transactionId)) {
                throw new IllegalArgumentException("Transaction ID không hợp lệ");
            }
            transactionService.removeOrder(transaction, productId);
            transaction = transactionService.getCartTransaction(user);
            if (transaction == null || transaction.getOrders() == null || transaction.getOrders().isEmpty()) {
                response.put("status", "success");
                response.put("message", "Xóa sản phẩm thành công");
                response.put("totalPrice", 0);
                response.put("cartCount", 0);
                response.put("totalUniqueQuantity", 0);
                response.put("isCartEmpty", true);
                response.put("transaction", null);
            } else {
                int totalPrice = transaction.getOrders().stream()
                        .mapToInt(order -> {
                            if (order == null || order.getPrice() == null || order.getQuantity() == null) return 0;
                            return order.getPrice() * order.getQuantity(); // Giá đã giảm
                        })
                        .sum();
                int totalUniqueQuantity = (int) transaction.getOrders().stream()
                        .filter(order -> order != null && order.getProduct() != null)
                        .map(order -> order.getProduct().getId())
                        .distinct()
                        .count();
                response.put("status", "success");
                response.put("message", "Xóa sản phẩm thành công");
                response.put("totalPrice", totalPrice);
                response.put("cartCount", totalUniqueQuantity);
                response.put("totalUniqueQuantity", totalUniqueQuantity);
                response.put("isCartEmpty", false);
            }
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.severe("Error removing cart item: " + e.getMessage());
            response.put("status", "error");
            response.put("message", "Lỗi khi xóa sản phẩm: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}