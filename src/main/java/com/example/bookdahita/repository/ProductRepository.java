package com.example.bookdahita.repository;

import com.example.bookdahita.models.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product,Long> {
    @Query("SELECT c.catname, COUNT(p) FROM Product p JOIN p.category c GROUP BY c.id, c.catname")
    List<Object[]> findProductCountByCategory();
}
