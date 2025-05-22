package com.example.bookdahita.repositoty;

import com.example.bookdahita.models.Transaction;
import com.example.bookdahita.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TransactionsRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByUser(User user);
}
