package com.example.bookdahita.repository;

import com.example.bookdahita.models.Transaction;
import com.example.bookdahita.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransactionsRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByUser(User user);
}
