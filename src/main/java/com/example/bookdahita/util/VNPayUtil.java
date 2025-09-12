package com.example.bookdahita.util;

import jakarta.servlet.http.HttpServletRequest;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.TreeMap;

public class VNPayUtil {

    public static String getIpAddress(HttpServletRequest request) {
        String ipAddress = request.getHeader("X-Forwarded-For");
        if (ipAddress == null || ipAddress.isEmpty()) {
            ipAddress = request.getRemoteAddr();
        }
        return ipAddress;
    }

    public static String getPaymentURL(Map<String, String> params, boolean encodeKey) {
        try {
            StringBuilder query = new StringBuilder();
            for (Map.Entry<String, String> entry : new TreeMap<>(params).entrySet()) {
                if (query.length() > 0) query.append("&");
                String key = encodeKey ? URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8.toString()) : entry.getKey();
                String value = URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8.toString());
                query.append(key).append("=").append(value);
            }
            return query.toString();
        } catch (Exception e) {
            throw new RuntimeException("Error building payment URL", e);
        }
    }

    public static String hmacSHA512(String key, String data) {
        try {
            Mac mac = Mac.getInstance("HmacSHA512");
            SecretKeySpec keySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA512");
            mac.init(keySpec);
            byte[] rawHmac = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : rawHmac) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            throw new RuntimeException("Error generating HMAC-SHA512", e);
        }
    }
}
