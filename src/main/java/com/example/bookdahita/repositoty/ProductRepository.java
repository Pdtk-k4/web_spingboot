package com.example.bookdahita.repositoty;

import com.example.bookdahita.models.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product,Long> {
}
