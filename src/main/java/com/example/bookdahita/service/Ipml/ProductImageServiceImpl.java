package com.example.bookdahita.service.Ipml;

import com.example.bookdahita.models.ProductsImages;
import com.example.bookdahita.repository.ProductsImagesRepository;
import com.example.bookdahita.service.ProductImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductImageServiceImpl implements ProductImageService {

    @Autowired
    private ProductsImagesRepository productsImagesRepository;

    @Override
    public List<ProductsImages> getAll() {
        return this.productsImagesRepository.findAll();
    }

    @Override
    public Boolean create(ProductsImages productsImages) {
        try {
            this.productsImagesRepository.save(productsImages);
            return true;
        }catch(Exception e) {
            return false;
        }
    }

    @Override
    public ProductsImages findById(Long id) {
        return this.productsImagesRepository.findById(id).get();
    }

    @Override
    public Boolean update(ProductsImages productsImages) {
        try {
            this.productsImagesRepository.save(productsImages);
            return true;
        }catch(Exception e) {
            return false;
        }
    }

    @Override
    public Boolean deleteByProductId(Long productId) {
        try {
            List<ProductsImages> images = productsImagesRepository.findAll().stream()
                    .filter(img -> img.getProduct() != null && img.getProduct().getId().equals(productId))
                    .toList();
            productsImagesRepository.deleteAll(images);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
