package com.example.bookdahita.repositoty;

import com.example.bookdahita.models.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartRepository extends JpaRepository<Transaction,Long> {
}
