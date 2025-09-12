package com.example.bookdahita.service;

import com.example.bookdahita.models.Product;

import java.util.List;

public interface ProductService {
    List<Product> getAll();
    Boolean create(Product product);
    Product findById(Long id);
    Boolean update(Product product);
    Boolean delete(Long id);
    List<Product> getNewActiveProductsLimitedToTen();
    List<Product> getProductsByCategoryId(Long categoryId);
    List<Product> getProductsOnSale();
    List<Product> getNewBooks();
    List<Product> getActiveProducts();
}