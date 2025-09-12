package com.example.bookdahita.service;

import com.example.bookdahita.models.Product;
import com.example.bookdahita.models.Transaction;
import com.example.bookdahita.models.User;

public interface TransactionService {
    Transaction createCartTransaction(User user);
    void addProductToTransaction(User user, Product product, int quantity);
    Transaction getCartTransaction(User user);
    void updateOrderQuantity(Transaction transaction, Long productId, int quantity);
    void removeOrder(Transaction transaction, Long productId);
    void deleteTransactionIfEmpty(Transaction transaction);
    Transaction getTransactionById(Long id);
    void placeOrder(Long transactionId, String name, String phone, String address, String paymentMethod);
    void deleteTransaction(Long transactionId);
    // Thêm phương thức mới để cập nhật trạng thái giao dịch
    void updateTransactionStatus(Long transactionId, String status);
}