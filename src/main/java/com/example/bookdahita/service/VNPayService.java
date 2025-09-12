package com.example.bookdahita.service;



import com.example.bookdahita.config.VNPayConfig;
import com.example.bookdahita.util.VNPayUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.Map;
import java.util.TreeMap;

@Service
public class VNPayService {

    private static final Logger logger = LoggerFactory.getLogger(VNPayService.class);

    @Autowired
    private VNPayConfig vnPayConfig;

    public String createPaymentUrl(HttpServletRequest request, long amount, String orderId, String orderInfo) {
        logger.debug("Creating VNPay payment URL - amount: {}, orderId: {}, orderInfo: {}", amount, orderId, orderInfo);

        if (amount <= 0) {
            logger.error("Invalid amount: {}", amount);
            throw new IllegalArgumentException("Số tiền phải lớn hơn 0");
        }
        if (orderId == null || orderId.trim().isEmpty()) {
            logger.error("Order ID is null or empty");
            throw new IllegalArgumentException("Mã giao dịch không được trống");
        }

        Map<String, String> vnpParamsMap = vnPayConfig.getVNPayConfig(orderId, orderInfo);
        vnpParamsMap.put("vnp_Amount", String.valueOf(amount * 100));
        vnpParamsMap.put("vnp_IpAddr", VNPayUtil.getIpAddress(request));

        String queryUrl = VNPayUtil.getPaymentURL(vnpParamsMap, true);
        String hashData = VNPayUtil.getPaymentURL(vnpParamsMap, false);
        String vnpSecureHash = VNPayUtil.hmacSHA512(vnPayConfig.getSecretKey(), hashData);
        queryUrl += "&vnp_SecureHash=" + vnpSecureHash;

        String paymentUrl = vnPayConfig.getVnp_PayUrl() + "?" + queryUrl;
        logger.debug("Generated payment URL: {}", paymentUrl);
        return paymentUrl;
    }

    public int verifyPaymentResponse(Map<String, String> params) {
        String vnp_SecureHash = params.get("vnp_SecureHash");
        params.remove("vnp_SecureHash");
        String signData = VNPayUtil.getPaymentURL(new TreeMap<>(params), false);
        String serverSign = VNPayUtil.hmacSHA512(vnPayConfig.getSecretKey(), signData);
        if (serverSign.equals(vnp_SecureHash)) {
            return Integer.parseInt(params.get("vnp_ResponseCode"));
        }
        return -1; // Ký không khớp
    }
}