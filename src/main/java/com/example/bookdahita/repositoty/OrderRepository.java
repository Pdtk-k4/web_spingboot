package com.example.bookdahita.repositoty;

import com.example.bookdahita.models.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
