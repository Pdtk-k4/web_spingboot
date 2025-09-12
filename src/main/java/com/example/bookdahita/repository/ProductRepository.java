package com.example.bookdahita.repository;

import com.example.bookdahita.models.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    Optional<Product> findById(Long id);

    @Query("SELECT p FROM Product p LEFT JOIN FETCH p.category c LEFT JOIN FETCH p.author a LEFT JOIN FETCH p.supplier s WHERE LOWER(p.proname) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(c.catname) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<Product> searchByNameOrCategory(String query);

    @Query("SELECT c.catname, COUNT(p) FROM Product p JOIN p.category c GROUP BY c.id, c.catname")
    List<Object[]> findProductCountByCategory();
}