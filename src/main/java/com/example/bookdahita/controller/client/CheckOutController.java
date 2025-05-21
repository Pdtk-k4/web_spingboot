
        package com.example.bookdahita.controller.client;

import com.example.bookdahita.models.CustomUserDetail;
import com.example.bookdahita.models.Transaction;
import com.example.bookdahita.models.User;
import com.example.bookdahita.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/client")
public class CheckOutController {

    @Autowired
    private TransactionService transactionService;

    @GetMapping("/checkouts")
    public String checkout(@RequestParam("id") Long transactionId,
                           @AuthenticationPrincipal CustomUserDetail userDetail,
                           Model model) {
        if (userDetail == null) {
            return "redirect:/client/login";
        }
        try {
            User user = userDetail.getUser();
            Transaction transaction = transactionService.getTransactionById(transactionId);

            // Kiểm tra transaction có thuộc về user không
            if (transaction == null || !transaction.getUser().getId().equals(user.getId())) {
                model.addAttribute("error", "Không tìm thấy đơn hàng hoặc bạn không có quyền truy cập");
                model.addAttribute("transaction", null);
                model.addAttribute("totalPrice", 0);
                model.addAttribute("totalUniqueQuantity", 0);
                return "client/checkouts";
            }

            if (transaction.getOrders() == null || transaction.getOrders().isEmpty()) {
                model.addAttribute("transaction", null);
                model.addAttribute("totalPrice", 0);
                model.addAttribute("totalUniqueQuantity", 0);
            } else {
                int totalPrice = transaction.getOrders().stream()
                        .mapToInt(order -> {
                            if (order == null || order.getPrice() == null || order.getQuantity() == null) return 0;
                            return order.getPrice() * order.getQuantity();
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
            return "client/checkouts";
        } catch (Exception e) {
            model.addAttribute("error", "Lỗi khi tải thông tin đơn hàng: " + e.getMessage());
            model.addAttribute("transaction", null);
            model.addAttribute("totalPrice", 0);
            model.addAttribute("totalUniqueQuantity", 0);
            return "client/checkouts";
        }
    }
}
