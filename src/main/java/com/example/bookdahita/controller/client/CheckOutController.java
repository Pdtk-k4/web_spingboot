package com.example.bookdahita.controller.client;

import com.example.bookdahita.models.CustomUserDetail;
import com.example.bookdahita.models.Inventory;
import com.example.bookdahita.models.Order;
import com.example.bookdahita.models.Transaction;
import com.example.bookdahita.models.User;
import com.example.bookdahita.repository.InboxRepository;
import com.example.bookdahita.repository.InventoryRepository;
import com.example.bookdahita.service.TransactionService;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/client")
public class CheckOutController {

    private static final Logger logger = LoggerFactory.getLogger(CheckOutController.class);

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private InboxRepository inboxRepository;

    @Autowired
    private InventoryRepository inventoryRepository;

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

    @PostMapping("/inventory/check")
    public ResponseEntity<Map<String, Object>> checkInventory(
            @AuthenticationPrincipal CustomUserDetail userDetail,
            @RequestParam("transactionId") Long transactionId
    ) {
        Map<String, Object> response = new HashMap<>();
        if (userDetail == null) {
            response.put("status", "error");
            response.put("message", "Vui lòng đăng nhập để kiểm tra tồn kho");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
        try {
            User user = userDetail.getUser();
            Transaction transaction = transactionService.getTransactionById(transactionId);
            if (transaction == null || transaction.getOrders() == null || transaction.getOrders().isEmpty()) {
                response.put("status", "error");
                response.put("message", "Giỏ hàng không tồn tại hoặc trống");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
            if (!transaction.getUser().getId().equals(user.getId())) {
                response.put("status", "error");
                response.put("message", "Bạn không có quyền truy cập đơn hàng này");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
            }

            // Kiểm tra tồn kho cho từng sản phẩm trong giỏ hàng
            for (Order order : transaction.getOrders()) {
                if (order == null || order.getProduct() == null) continue;
                Long productId = order.getProduct().getId();
                int quantity = order.getQuantity();
                Inventory inventory = inventoryRepository.findByProductId(productId)
                        .orElse(Inventory.builder().quantity(0).build());
                int inventoryQuantity = inventory.getQuantity();
                if (quantity > inventoryQuantity) {
                    response.put("status", "error");
                    response.put("message", "Sản phẩm " + order.getProduct().getProname() + " chỉ còn " + inventoryQuantity + " trong kho.");
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
                }
            }

            response.put("status", "success");
            response.put("message", "Tồn kho đủ để thanh toán");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error in checkInventory for transactionId: {}", transactionId, e);
            response.put("status", "error");
            response.put("message", "Lỗi khi kiểm tra tồn kho: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
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
                return "client/checkouts";
            }

            // Kiểm tra tồn kho trước khi hiển thị trang thanh toán
            for (Order order : transaction.getOrders()) {
                if (order == null || order.getProduct() == null) continue;
                Inventory inventory = inventoryRepository.findByProductId(order.getProduct().getId())
                        .orElse(Inventory.builder().quantity(0).build());
                if (order.getQuantity() > inventory.getQuantity()) {
                    logger.warn("Insufficient inventory for product: {}, requested: {}, available: {}",
                            order.getProduct().getProname(), order.getQuantity(), inventory.getQuantity());
                    model.addAttribute("error", "Sản phẩm " + order.getProduct().getProname() + " chỉ còn " + inventory.getQuantity() + " trong kho.");
                    return "client/cart";
                }
            }

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

            // Kiểm tra tồn kho trước khi đặt hàng
            for (Order order : transaction.getOrders()) {
                if (order == null || order.getProduct() == null) continue;
                Inventory inventory = inventoryRepository.findByProductId(order.getProduct().getId())
                        .orElse(Inventory.builder().quantity(0).build());
                if (order.getQuantity() > inventory.getQuantity()) {
                    logger.warn("Insufficient inventory for product: {}, requested: {}, available: {}",
                            order.getProduct().getProname(), order.getQuantity(), inventory.getQuantity());
                    model.addAttribute("error", "Sản phẩm " + order.getProduct().getProname() + " chỉ còn " + inventory.getQuantity() + " trong kho.");
                    return "client/checkouts";
                }
            }

            // Giảm tồn kho với optimistic locking
            for (Order order : transaction.getOrders()) {
                if (order == null || order.getProduct() == null) continue;
                Inventory inventory = inventoryRepository.findByProductId(order.getProduct().getId())
                        .orElseThrow(() -> new IllegalStateException("Không tìm thấy tồn kho cho sản phẩm: " + order.getProduct().getProname()));
                inventory.setQuantity(inventory.getQuantity() - order.getQuantity());
                inventoryRepository.save(inventory); // Optimistic locking sẽ ném ObjectOptimisticLockingFailureException nếu xung đột
            }

            // Đặt hàng
            logger.info("Calling placeOrder for transactionId: {}, name: {}, phone: {}, address: {}, payment: {}",
                    transactionId, name, phone, finalAddress, paymentMethod);
            transactionService.placeOrder(transactionId, name, phone, finalAddress, paymentMethod);
            logger.info("Order placed successfully, redirecting to cart for transactionId: {}", transactionId);
            return "redirect:/client/cart";
        } catch (ObjectOptimisticLockingFailureException e) {
            logger.error("Optimistic locking failure during placeOrder for transactionId: {}", transactionId, e);
            model.addAttribute("error", "Sản phẩm trong giỏ hàng đã được người khác đặt mua. Vui lòng kiểm tra lại giỏ hàng.");
            model.addAttribute("transaction", null);
            model.addAttribute("totalPrice", 0);
            model.addAttribute("totalUniqueQuantity", 0);
            return "client/checkouts";
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