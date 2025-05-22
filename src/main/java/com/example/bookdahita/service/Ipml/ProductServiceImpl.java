package com.example.bookdahita.service.Ipml;

import com.example.bookdahita.models.Product;
import com.example.bookdahita.repository.ProductRepository;
import com.example.bookdahita.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Override
    public List<Product> getAll() {
        return this.productRepository.findAll();
    }

    @Override
    public Boolean create(Product product) {
        try {
            this.productRepository.save(product);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public Product findById(Long id) {
        return this.productRepository.findById(id).orElse(null);
    }

    @Override
    public Boolean update(Product product) {
        try {
            this.productRepository.save(product);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public Boolean delete(Long id) {
        try {
            this.productRepository.deleteById(id);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public List<Product> getNewActiveProductsLimitedToTen() {
        return this.productRepository.findAll().stream()
                .filter(product -> product.getPronewbook() != null && product.getPronewbook()
                        && product.getProstatus() != null && product.getProstatus())
                .limit(10)
                .collect(Collectors.toList());
    }

    @Override
    public List<Product> getProductsByCategoryId(Long categoryId) {
        return this.productRepository.findAll().stream()
                .filter(product -> product.getCategory() != null && product.getCategory().getId().equals(categoryId)
                        && product.getProstatus() != null && product.getProstatus())
                .collect(Collectors.toList());
    }

    @Override
    public List<Product> getProductsOnSale() {
        return this.productRepository.findAll().stream()
                .filter(product -> product.getProsale() != null && product.getProsale() > 0)
                .collect(Collectors.toList());
    }

    @Override
    public List<Product> getNewBooks() {
        return this.productRepository.findAll().stream()
                .filter(product -> product.getPronewbook() != null && product.getPronewbook())
                .collect(Collectors.toList());
    }

    @Override
    public List<Product> getActiveProducts() {
        return this.productRepository.findAll().stream()
                .filter(product -> product.getProstatus() != null && product.getProstatus())
                .limit(5)
                .collect(Collectors.toList());
    }
}