package com.example.bookdahita.service.Ipml;

import com.example.bookdahita.models.Order;
import com.example.bookdahita.models.Product;
import com.example.bookdahita.models.Transaction;
import com.example.bookdahita.models.User;
import com.example.bookdahita.repositoty.OrderRepository;
import com.example.bookdahita.repositoty.ProductRepository;
import com.example.bookdahita.repositoty.TransactionsRepository;
import com.example.bookdahita.service.TransactionService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Optional;

@Service
@Transactional
public class TransactionServiceImpl implements TransactionService {

    @Autowired
    private TransactionsRepository transactionsRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;

    @Override
    public Transaction createCartTransaction(User user) {
        Optional<Transaction> existingCart = transactionsRepository.findByUserAndNote(user, "CART");
        if (existingCart.isPresent()) {
            return existingCart.get();
        }
        Transaction transaction = new Transaction();
        transaction.setUser(user);
        transaction.setTotalMoney(0);
        transaction.setNote("CART");
        transaction.setOrders(new HashSet<>());
        return transactionsRepository.save(transaction);
    }

    @Override
    public void addProductToTransaction(User user, Product product, int quantity) {
        Transaction transaction = createCartTransaction(user);
        Optional<Order> existingOrder = transaction.getOrders().stream()
                .filter(order -> order.getProduct().getId().equals(product.getId()))
                .findFirst();
        if (existingOrder.isPresent()) {
            Order order = existingOrder.get();
            order.setQuantity(order.getQuantity() + quantity);
            order.setPrice(product.getProprice());
            orderRepository.save(order);
        } else {
            Order order = new Order();
            order.setTransaction(transaction);
            order.setProduct(product);
            order.setQuantity(quantity);
            order.setPrice(product.getProprice());
            transaction.getOrders().add(order);
            orderRepository.save(order);
        }
        updateTotalMoney(transaction);
        transactionsRepository.save(transaction);
    }

    @Override
    public Transaction getCartTransaction(User user) {
        return transactionsRepository.findByUserAndNote(user, "CART").orElse(null);
    }

    @Override
    public void updateOrderQuantity(Transaction transaction, Long productId, int quantity) {
        if (quantity < 1) {
            throw new IllegalArgumentException("Số lượng phải lớn hơn hoặc bằng 1");
        }
        Optional<Order> existingOrder = transaction.getOrders().stream()
                .filter(order -> order.getProduct().getId().equals(productId))
                .findFirst();
        if (existingOrder.isPresent()) {
            Order order = existingOrder.get();
            order.setQuantity(quantity);
            orderRepository.save(order);
            updateTotalMoney(transaction);
            transactionsRepository.save(transaction);
        } else {
            throw new IllegalArgumentException("Không tìm thấy sản phẩm trong giỏ hàng");
        }
    }

    @Override
    public void removeOrder(Transaction transaction, Long productId) {
        Optional<Order> orderToRemove = transaction.getOrders().stream()
                .filter(order -> order.getProduct().getId().equals(productId))
                .findFirst();
        if (orderToRemove.isPresent()) {
            Order order = orderToRemove.get();
            transaction.getOrders().remove(order);
            orderRepository.delete(order);
            updateTotalMoney(transaction);
            transactionsRepository.save(transaction);
            deleteTransactionIfEmpty(transaction);
        } else {
            throw new IllegalArgumentException("Không tìm thấy sản phẩm trong giỏ hàng");
        }
    }

    @Override
    public void deleteTransactionIfEmpty(Transaction transaction) {
        if (transaction.getOrders().isEmpty()) {
            transactionsRepository.delete(transaction);
        }
    }

    @Override
    public Transaction getTransactionById(Long id) {
        return transactionsRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy giao dịch với ID: " + id));
    }

    private void updateTotalMoney(Transaction transaction) {
        int total = transaction.getOrders().stream()
                .mapToInt(order -> order.getPrice() * order.getQuantity())
                .sum();
        transaction.setTotalMoney(total);
    }
}