package com.example.bookdahita.service;

import com.example.bookdahita.models.ProductsImages;

import java.util.List;

public interface ProductImageService {
    List<ProductsImages> getAll();
    Boolean create(ProductsImages productsImages);
    ProductsImages findById(Long id);
    Boolean update(ProductsImages productsImages);
    Boolean deleteByProductId(Long productId);
}
