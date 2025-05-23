package com.example.bookdahita.controller.client;


import com.example.bookdahita.service.TransactionService;
import com.example.bookdahita.service.VNPayService;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;


import java.util.Map;

@Controller
@RequestMapping("/api/vnpay")
public class VNPayController {

    private static final Logger logger = LoggerFactory.getLogger(VNPayController.class);

    @Autowired
    private VNPayService vnPayService;

    @Autowired
    private TransactionService transactionService;

    public static class ResponseObject<T> {
        private HttpStatus status;
        private String message;
        private T data;

        public ResponseObject(HttpStatus status, String message, T data) {
            this.status = status;
            this.message = message;
            this.data = data;
        }

        public HttpStatus getStatus() {
            return status;
        }

        public String getMessage() {
            return message;
        }

        public T getData() {
            return data;
        }
    }

    public static class VNPayResponse {
        public String code;
        public String message;
        public String paymentUrl;

        public VNPayResponse(String code, String message, String paymentUrl) {
            this.code = code;
            this.message = message;
            this.paymentUrl = paymentUrl;
        }
    }

    @PostMapping("/create-payment")
    public @ResponseBody ResponseObject<VNPayResponse> createPayment(HttpServletRequest request,
                                                                     @RequestParam("amount") long amount,
                                                                     @RequestParam("orderId") String orderId,
                                                                     @RequestParam("orderInfo") String orderInfo) throws Exception {
        logger.debug("Received create-payment request: amount={}, orderId={}, orderInfo={}", amount, orderId, orderInfo);
        String paymentUrl = vnPayService.createPaymentUrl(request, amount, orderId, orderInfo);
        VNPayResponse response = new VNPayResponse("ok", "success", paymentUrl);
        return new ResponseObject<>(HttpStatus.OK, "Success", response);
    }

    @GetMapping("/callback")
    public @ResponseBody ResponseObject<VNPayResponse> paymentCallback(@RequestParam Map<String, String> params) {
        logger.debug("Received VNPay callback with params: {}", params);
        int responseCode = vnPayService.verifyPaymentResponse(params);
        String transactionId = params.get("vnp_TxnRef");

        if (responseCode == 0) {
            transactionService.updateTransactionStatus(Long.parseLong(transactionId), "Paid");
            return new ResponseObject<>(HttpStatus.OK, "Success", new VNPayResponse("00", "Success", ""));
        } else {
            transactionService.updateTransactionStatus(Long.parseLong(transactionId), "Failed");
            return new ResponseObject<>(HttpStatus.BAD_REQUEST, "Failed", new VNPayResponse(String.valueOf(responseCode), "Failed", ""));
        }
    }
}