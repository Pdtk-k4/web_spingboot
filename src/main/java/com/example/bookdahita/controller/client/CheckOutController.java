package com.example.bookdahita.controller.client;

import com.example.bookdahita.models.CustomUserDetail;
import com.example.bookdahita.models.Transaction;
import com.example.bookdahita.models.User;
import com.example.bookdahita.repository.InboxRepository;
import com.example.bookdahita.service.TransactionService;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/client")
public class CheckOutController {

    private static final Logger logger = LoggerFactory.getLogger(CheckOutController.class);

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private InboxRepository inboxRepository;

    private boolean validateAndPrepareModel(Long transactionId, CustomUserDetail userDetail, Model model) {
        if (userDetail == null) {
            logger.warn("User not authenticated, redirecting to login");
            model.addAttribute("error", "Vui lòng đăng nhập để tiếp tục");
            return false;
        }
        if (transactionId == null || transactionId <= 0) {
            logger.error("Invalid transaction ID: {}", transactionId);
            model.addAttribute("error", "ID đơn hàng không hợp lệ");
            return false;
        }
        User user = userDetail.getUser();
        if (user == null) {
            logger.error("User object is null for transactionId: {}", transactionId);
            model.addAttribute("error", "Không tìm thấy thông tin người dùng");
            return false;
        }
        return true;
    }

    @GetMapping("/checkouts")
    public String checkout(@RequestParam(value = "id", required = false) Long transactionId,
                           @AuthenticationPrincipal CustomUserDetail userDetail,
                           Model model) {
        logger.debug("Processing checkout for transactionId: {}", transactionId);
        if (!validateAndPrepareModel(transactionId, userDetail, model)) {
            model.addAttribute("transaction", null);
            model.addAttribute("totalPrice", 0);
            model.addAttribute("totalUniqueQuantity", 0);
            return "client/checkouts";
        }
        try {
            User user = userDetail.getUser();
            Transaction transaction = transactionService.getTransactionById(transactionId);
            if (!transaction.getUser().getId().equals(user.getId())) {
                logger.error("Transaction not found or user unauthorized for transactionId: {}", transactionId);
                model.addAttribute("error", "Không tìm thấy đơn hàng hoặc bạn không có quyền truy cập");
                model.addAttribute("transaction", null);
                model.addAttribute("totalPrice", 0);
                model.addAttribute("totalUniqueQuantity", 0);
                return "client/checkouts";
            }
            if (transaction.getOrders() == null || transaction.getOrders().isEmpty()) {
                logger.warn("Empty cart for transactionId: {}", transactionId);
                model.addAttribute("error", "Giỏ hàng trống");
                model.addAttribute("transaction", null);
                model.addAttribute("totalPrice", 0);
                model.addAttribute("totalUniqueQuantity", 0);
            } else {
                int totalPrice = transaction.getOrders().stream()
                        .filter(order -> order != null && order.getPrice() != null && order.getQuantity() != null)
                        .mapToInt(order -> order.getPrice() * order.getQuantity())
                        .sum();
                int totalUniqueQuantity = (int) transaction.getOrders().stream()
                        .filter(order -> order != null && order.getProduct() != null)
                        .map(order -> order.getProduct().getId())
                        .distinct()
                        .count();
                model.addAttribute("transaction", transaction);
                model.addAttribute("totalPrice", totalPrice);
                model.addAttribute("totalUniqueQuantity", totalUniqueQuantity);
                logger.debug("Checkout loaded successfully for transactionId: {}", transactionId);
            }
            return "client/checkouts";
        } catch (EntityNotFoundException e) {
            logger.error("EntityNotFoundException for transactionId: {}", transactionId, e);
            model.addAttribute("error", "Không tìm thấy đơn hàng: " + e.getMessage());
            model.addAttribute("transaction", null);
            model.addAttribute("totalPrice", 0);
            model.addAttribute("totalUniqueQuantity", 0);
            return "client/checkouts";
        } catch (Exception e) {
            logger.error("Unexpected error during checkout for transactionId: {}", transactionId, e);
            model.addAttribute("error", "Lỗi khi tải thông tin đơn hàng: " + e.getMessage());
            model.addAttribute("transaction", null);
            model.addAttribute("totalPrice", 0);
            model.addAttribute("totalUniqueQuantity", 0);
            return "client/checkouts";
        }
    }

    @PostMapping("/order")
    public String placeOrder(@RequestParam(value = "transactionId", required = false) Long transactionId,
                             @RequestParam("name") String name,
                             @RequestParam("phone") String phone,
                             @RequestParam(value = "addressDetail", required = false) String addressDetail,
                             @RequestParam(value = "address", required = false) String address,
                             @RequestParam("payment") String paymentMethod,
                             @AuthenticationPrincipal CustomUserDetail userDetail,
                             Model model) {
        logger.debug("Processing placeOrder for transactionId: {}", transactionId);
        if (!validateAndPrepareModel(transactionId, userDetail, model)) {
            model.addAttribute("transaction", null);
            model.addAttribute("totalPrice", 0);
            model.addAttribute("totalUniqueQuantity", 0);
            return "client/checkouts";
        }
        try {
            User user = userDetail.getUser();
            Transaction transaction = transactionService.getTransactionById(transactionId);
            if (!transaction.getUser().getId().equals(user.getId())) {
                logger.error("User unauthorized for transactionId: {}", transactionId);
                model.addAttribute("error", "Bạn không có quyền đặt đơn hàng này");
                model.addAttribute("transaction", null);
                model.addAttribute("totalPrice", 0);
                model.addAttribute("totalUniqueQuantity", 0);
                return "client/checkouts";
            }
            String finalAddress = StringUtils.hasText(addressDetail) ? addressDetail : address;
            if (!StringUtils.hasText(finalAddress)) {
                logger.warn("Empty address for transactionId: {}", transactionId);
                model.addAttribute("error", "Địa chỉ giao hàng không được để trống");
                model.addAttribute("transaction", transaction);
                model.addAttribute("totalPrice", transaction.getTotalMoney());
                model.addAttribute("totalUniqueQuantity", transaction.getOrders().size());
                return "client/checkouts";
            }
            if (!StringUtils.hasText(name) || !StringUtils.hasText(phone)) {
                logger.warn("Invalid name or phone for transactionId: {}", transactionId);
                model.addAttribute("error", "Họ tên và số điện thoại không được để trống");
                model.addAttribute("transaction", transaction);
                model.addAttribute("totalPrice", transaction.getTotalMoney());
                model.addAttribute("totalUniqueQuantity", transaction.getOrders().size());
                return "client/checkouts";
            }
            logger.info("Calling placeOrder for transactionId: {}, name: {}, phone: {}, address: {}, payment: {}",
                    transactionId, name, phone, finalAddress, paymentMethod);
            transactionService.placeOrder(transactionId, name, phone, finalAddress, paymentMethod);
            logger.info("Order placed successfully, redirecting to cart for transactionId: {}", transactionId);
            return "redirect:/client/cart";
        } catch (EntityNotFoundException e) {
            logger.error("EntityNotFoundException during placeOrder for transactionId: {}", transactionId, e);
            model.addAttribute("error", "Không tìm thấy đơn hàng: " + e.getMessage());
            model.addAttribute("transaction", null);
            model.addAttribute("totalPrice", 0);
            model.addAttribute("totalUniqueQuantity", 0);
            return "client/checkouts";
        } catch (IllegalStateException e) {
            logger.error("IllegalStateException during placeOrder for transactionId: {}", transactionId, e);
            model.addAttribute("error", e.getMessage());
            model.addAttribute("transaction", null);
            model.addAttribute("totalPrice", 0);
            model.addAttribute("totalUniqueQuantity", 0);
            return "client/checkouts";
        } catch (Exception e) {
            logger.error("Unexpected error during placeOrder for transactionId: {}", transactionId, e);
            model.addAttribute("error", "Lỗi khi đặt hàng: Vui lòng kiểm tra thông tin và thử lại");
            model.addAttribute("transaction", null);
            model.addAttribute("totalPrice", 0);
            model.addAttribute("totalUniqueQuantity", 0);
            return "client/checkouts";
        }
    }
}