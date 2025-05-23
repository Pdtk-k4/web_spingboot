package com.example.bookdahita.service.Ipml;

import com.example.bookdahita.models.*;
import com.example.bookdahita.repository.InboxRepository;
import com.example.bookdahita.repository.OrderRepository;
import com.example.bookdahita.repository.ProductRepository;
import com.example.bookdahita.repository.TransactionsRepository;
import com.example.bookdahita.repository.DetailInboxRepository;
import com.example.bookdahita.service.TransactionService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class TransactionServiceImpl implements TransactionService {

    private static final Logger logger = LoggerFactory.getLogger(TransactionServiceImpl.class);

    @Autowired
    private TransactionsRepository transactionsRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private InboxRepository inboxRepository;

    @Autowired
    private DetailInboxRepository detailInboxRepository;

    @Override
    public Transaction createCartTransaction(User user) {
        logger.debug("Creating transaction for user: {}", user.getId());
        Transaction transaction = new Transaction();
        transaction.setUser(user);
        transaction.setTotalMoney(0);
        transaction.setNote("");
        transaction.setOrders(new HashSet<>());
        transaction.setStatus("Pending"); // Đặt trạng thái ban đầu là Pending
        Transaction saved = transactionsRepository.save(transaction);
        logger.debug("Created new transaction: {}", saved.getId());
        return saved;
    }

    @Override
    public void addProductToTransaction(User user, Product product, int quantity) {
        logger.debug("Adding product {} with quantity {} to transaction for user: {}", product.getId(), quantity, user.getId());
        if (product == null) {
            logger.error("Product is null for user: {}", user.getId());
            throw new IllegalArgumentException("Sản phẩm không hợp lệ");
        }

        List<Transaction> userTransactions = transactionsRepository.findByUser(user);
        Transaction transaction;
        if (userTransactions.isEmpty()) {
            logger.debug("No transactions found for user: {}, creating new transaction", user.getId());
            transaction = createCartTransaction(user);
        } else {
            transaction = userTransactions.get(0);
            logger.debug("Using existing transaction: {}", transaction.getId());
        }

        // Tính giá đã giảm
        int originalPrice = product.getProprice();
        int discountPercentage = product.getProsale() != null ? product.getProsale() : 0;
        int discountedPrice = originalPrice - (originalPrice * discountPercentage / 100);

        Optional<Order> existingOrder = transaction.getOrders().stream()
                .filter(order -> order.getProduct() != null && order.getProduct().getId().equals(product.getId()))
                .findFirst();
        if (existingOrder.isPresent()) {
            Order order = existingOrder.get();
            order.setQuantity(order.getQuantity() + quantity);
            order.setPrice(discountedPrice); // Lưu giá đã giảm
            orderRepository.save(order);
            logger.debug("Updated existing order: {}", order.getId());
        } else {
            Order order = new Order();
            order.setTransaction(transaction);
            order.setProduct(product);
            order.setQuantity(quantity);
            order.setPrice(discountedPrice); // Lưu giá đã giảm
            transaction.getOrders().add(order);
            orderRepository.save(order);
            logger.debug("Created new order: {}", order.getId());
        }
        updateTotalMoney(transaction);
        transactionsRepository.save(transaction);
    }

    @Override
    public Transaction getCartTransaction(User user) {
        logger.debug("Getting transaction for user: {}", user.getId());
        List<Transaction> userTransactions = transactionsRepository.findByUser(user);
        if (!userTransactions.isEmpty()) {
            Transaction transaction = userTransactions.get(0);
            logger.debug("Transaction found: {}", transaction.getId());
            return transaction;
        }
        logger.debug("No transaction found, creating new transaction for user: {}", user.getId());
        return createCartTransaction(user);
    }

    @Override
    public void updateOrderQuantity(Transaction transaction, Long productId, int quantity) {
        logger.debug("Updating quantity to {} for product {} in transaction: {}", quantity, productId, transaction.getId());
        if (quantity < 1) {
            throw new IllegalArgumentException("Số lượng phải lớn hơn hoặc bằng 1");
        }
        Optional<Order> existingOrder = transaction.getOrders().stream()
                .filter(order -> order.getProduct() != null && order.getProduct().getId().equals(productId))
                .findFirst();
        if (existingOrder.isPresent()) {
            Order order = existingOrder.get();
            order.setQuantity(quantity);
            // Cập nhật giá đã giảm
            int originalPrice = order.getProduct().getProprice();
            int discountPercentage = order.getProduct().getProsale() != null ? order.getProduct().getProsale() : 0;
            int discountedPrice = originalPrice - (originalPrice * discountPercentage / 100);
            order.setPrice(discountedPrice);
            orderRepository.save(order);
            updateTotalMoney(transaction);
            transactionsRepository.save(transaction);
            logger.debug("Updated order quantity for product: {}", productId);
        } else {
            throw new IllegalArgumentException("Không tìm thấy sản phẩm trong giao dịch");
        }
    }

    @Override
    public void removeOrder(Transaction transaction, Long productId) {
        logger.debug("Removing product {} from transaction: {}", productId, transaction.getId());
        Optional<Order> orderToRemove = transaction.getOrders().stream()
                .filter(order -> order.getProduct() != null && order.getProduct().getId().equals(productId))
                .findFirst();
        if (orderToRemove.isPresent()) {
            Order order = orderToRemove.get();
            transaction.getOrders().remove(order);
            orderRepository.delete(order);
            updateTotalMoney(transaction);
            transactionsRepository.save(transaction);
            deleteTransactionIfEmpty(transaction);
            logger.debug("Removed order for product: {}", productId);
        } else {
            throw new IllegalArgumentException("Không tìm thấy sản phẩm trong giao dịch");
        }
    }

    @Override
    public void deleteTransactionIfEmpty(Transaction transaction) {
        logger.debug("Checking if transaction {} is empty", transaction.getId());
        if (transaction.getOrders() == null || transaction.getOrders().isEmpty()) {
            transactionsRepository.delete(transaction);
            logger.debug("Deleted empty transaction: {}", transaction.getId());
        }
    }

    @Override
    public Transaction getTransactionById(Long id) {
        logger.debug("Getting transaction by ID: {}", id);
        Transaction transaction = transactionsRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy giao dịch với ID: " + id));
        logger.debug("Found transaction: {}", id);
        return transaction;
    }

    @Override
    public void deleteTransaction(Long transactionId) {
        logger.debug("Deleting transaction: {}", transactionId);
        Transaction transaction = getTransactionById(transactionId);
        transaction.getOrders().forEach(order -> {
            logger.debug("Deleting order: {}", order.getId());
            orderRepository.delete(order);
        });
        transaction.getOrders().clear();
        transactionsRepository.save(transaction); // Lưu trước khi xóa
        transactionsRepository.delete(transaction);
        logger.debug("Deleted transaction: {}", transactionId);
    }

    private void updateTotalMoney(Transaction transaction) {
        logger.debug("Updating total money for transaction: {}", transaction.getId());
        int total = transaction.getOrders().stream()
                .mapToInt(order -> {
                    if (order == null || order.getPrice() == null || order.getQuantity() == null) return 0;
                    return order.getPrice() * order.getQuantity();
                })
                .sum();
        transaction.setTotalMoney(total);
        logger.debug("Total money updated to: {}", total);
    }

    @Override
    public void placeOrder(Long transactionId, String name, String phone, String address, String paymentMethod) {
        logger.debug("Placing order for transaction: {}", transactionId);
        Transaction transaction = getTransactionById(transactionId);
        if (transaction.getOrders() == null || transaction.getOrders().isEmpty()) {
            logger.error("Empty transaction, cannot place order for transaction: {}", transactionId);
            throw new IllegalStateException("Giao dịch trống, không thể đặt hàng");
        }

        int totalQuantity = transaction.getOrders().stream()
                .mapToInt(Order::getQuantity)
                .sum();
        logger.debug("Total quantity: {}", totalQuantity);

        Inbox inbox = new Inbox();
        inbox.setUser(transaction.getUser());
        inbox.setQuantity(totalQuantity);
        inbox.setPrice(transaction.getTotalMoney());
        inbox.setCustomerName(name);
        inbox.setCustomerAddress(address);
        inbox.setCustomerPhone(phone);
        inbox.setStatus(false);
        inbox.setTransaction(null);

        try {
            Inbox savedInbox = inboxRepository.save(inbox);
            logger.debug("Saved inbox for transaction: {}", transactionId);

            for (Order order : transaction.getOrders()) {
                DetailInbox detailInbox = new DetailInbox();
                detailInbox.setProduct(order.getProduct());
                detailInbox.setUser(transaction.getUser());
                detailInbox.setInboxId(savedInbox.getId());
                detailInbox.setQuantity(order.getQuantity());
                detailInboxRepository.save(detailInbox);
                logger.debug("Saved DetailInbox for product: {} and inbox: {}", order.getProduct().getId(), savedInbox.getId());
            }

            // Chỉ xóa giao dịch nếu thanh toán COD
            if ("cod".equalsIgnoreCase(paymentMethod)) {
                transaction.getOrders().forEach(order -> {
                    logger.debug("Deleting order: {}", order.getId());
                    orderRepository.delete(order);
                });
                transaction.getOrders().clear();
                transactionsRepository.save(transaction);
                logger.debug("Cleared all orders for transaction: {}", transactionId);

                transactionsRepository.delete(transaction);
                logger.debug("Deleted transaction: {}", transactionId);
            } else if ("vnpay".equalsIgnoreCase(paymentMethod)) {
                // Giữ giao dịch lại để xử lý callback từ VNPay
                transaction.setStatus("PendingPayment");
                transactionsRepository.save(transaction);
                logger.debug("Updated transaction status to PendingPayment for VNPay: {}", transactionId);
            }
        } catch (Exception e) {
            logger.error("Error processing order for transaction: {}", transactionId, e);
            throw new RuntimeException("Lỗi khi xử lý đơn hàng: " + e.getMessage(), e);
        }
    }

    @Override
    public void updateTransactionStatus(Long transactionId, String status) {
        logger.debug("Updating status to {} for transaction: {}", status, transactionId);
        Transaction transaction = transactionsRepository.findById(transactionId)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy giao dịch với ID: " + transactionId));
        transaction.setStatus(status);
        transactionsRepository.save(transaction);
        logger.debug("Updated transaction status to: {}", status);

        // Nếu giao dịch thành công (Paid), chuyển dữ liệu sang Inbox và xóa giao dịch
        if ("Paid".equalsIgnoreCase(status)) {
            // Tìm Inbox phù hợp
            Inbox inbox = inboxRepository.findByUser(transaction.getUser())
                    .stream()
                    .filter(i -> i.getTransaction() == null)
                    .findFirst()
                    .orElse(null);

            if (inbox == null) {
                // Nếu không tìm thấy Inbox, tạo mới
                logger.debug("No matching Inbox found for transaction: {}, creating new Inbox", transactionId);
                inbox = new Inbox();
                inbox.setUser(transaction.getUser());
                inbox.setQuantity(transaction.getOrders().stream().mapToInt(Order::getQuantity).sum());
                inbox.setPrice(transaction.getTotalMoney());
                inbox.setCustomerName(""); // Cần lấy thông tin từ transaction hoặc form
                inbox.setCustomerAddress("");
                inbox.setCustomerPhone("");
                inbox.setStatus(false);
                inbox.setTransaction(null);
                inboxRepository.save(inbox);
            }

            inbox.setTransaction(transaction);
            inbox.setStatus(true); // Đánh dấu đơn hàng đã thanh toán
            inboxRepository.save(inbox);
            logger.debug("Updated Inbox status for transaction: {}", transactionId);

            // Xóa giao dịch sau khi thanh toán thành công
            deleteTransaction(transactionId);
        } else if ("Failed".equalsIgnoreCase(status)) {
            // Giữ giao dịch để người dùng thử lại
            logger.warn("Transaction {} failed, keeping transaction for retry", transactionId);
            // Có thể thêm logic thông báo người dùng, ví dụ: gửi email hoặc lưu thông báo
        }
    }
}