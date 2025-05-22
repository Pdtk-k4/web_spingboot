package com.example.bookdahita.controller.client;

import com.example.bookdahita.service.VNPayService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/vnpay")
public class VNPayController {

    @Autowired
    private VNPayService vnPayService;

    @PostMapping("/create-payment")
    public ResponseEntity<String> createPayment(HttpServletRequest request,
                                                @RequestParam("amount") long amount,
                                                @RequestParam("orderId") String orderId,
                                                @RequestParam("orderInfo") String orderInfo) throws Exception {
        String paymentUrl = vnPayService.createPaymentUrl(request, amount, orderId, orderInfo);
        return ResponseEntity.ok(paymentUrl);
    }

    @GetMapping("/callback")
    public ResponseEntity<String> paymentCallback(@RequestParam Map<String, String> params) {
        System.out.println("Callback received with params: " + params);
        boolean isValid = vnPayService.verifyPaymentResponse(params);
        if (isValid && "00".equals(params.get("vnp_ResponseCode"))) {
            return ResponseEntity.ok("Thanh toán thành công!");
        } else {
            return ResponseEntity.badRequest().body("Thanh toán thất bại hoặc chữ ký không hợp lệ!");
        }
    }
}
