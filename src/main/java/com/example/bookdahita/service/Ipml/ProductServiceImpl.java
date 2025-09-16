package com.example.bookdahita.service.Ipml;

import com.example.bookdahita.models.Product;
import com.example.bookdahita.repository.AuthorRepository;
import com.example.bookdahita.repository.CategoryRepository;
import com.example.bookdahita.repository.ProductRepository;
import com.example.bookdahita.repository.SupplierRepository;
import com.example.bookdahita.service.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService {

    private static final Logger logger = LoggerFactory.getLogger(ProductServiceImpl.class);

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private SupplierRepository supplierRepository;

    @Override
    public List<Product> getAll() {
        logger.info("Lấy danh sách tất cả sản phẩm");
        return this.productRepository.findAll();
    }

    @Override
    public Boolean create(Product product) {
        try {
            logger.info("Tạo sản phẩm mới: {}", product.getProname());
            validateProduct(product);
            this.productRepository.save(product);
            logger.info("Tạo sản phẩm thành công: {}", product.getProname());
            return true;
        } catch (IllegalArgumentException e) {
            logger.error("Lỗi dữ liệu khi tạo sản phẩm: {}", e.getMessage());
            return false;
        } catch (Exception e) {
            logger.error("Lỗi không xác định khi tạo sản phẩm: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public Product findById(Long id) {
        logger.info("Tìm sản phẩm với ID: {}", id);
        return this.productRepository.findById(id).orElse(null);
    }

    @Override
    public Boolean update(Product product) {
        try {
            logger.info("Bắt đầu cập nhật sản phẩm ID: {}", product.getId());
            // Kiểm tra sản phẩm tồn tại
            Product existingProduct = productRepository.findById(product.getId())
                    .orElseThrow(() -> {
                        logger.error("Sản phẩm không tồn tại với ID: {}", product.getId());
                        return new IllegalArgumentException("Sản phẩm không tồn tại");
                    });

            // Kiểm tra và ánh xạ các thực thể liên quan
            validateProduct(product);

            // Cập nhật các thuộc tính
            existingProduct.setProname(product.getProname());
            existingProduct.setProcontent(product.getProcontent());
            existingProduct.setProprice(product.getProprice());
            existingProduct.setProsale(product.getProsale());
            existingProduct.setPronewbook(product.getPronewbook());
            existingProduct.setProstatus(product.getProstatus());
            existingProduct.setProimage(product.getProimage());
            existingProduct.setCategory(categoryRepository.findById(product.getCategory().getId())
                    .orElseThrow(() -> {
                        logger.error("Danh mục không tồn tại với ID: {}", product.getCategory().getId());
                        return new IllegalArgumentException("Danh mục không tồn tại");
                    }));
            existingProduct.setAuthor(authorRepository.findById(product.getAuthor().getId())
                    .orElseThrow(() -> {
                        logger.error("Tác giả không tồn tại với ID: {}", product.getAuthor().getId());
                        return new IllegalArgumentException("Tác giả không tồn tại");
                    }));
            existingProduct.setSupplier(supplierRepository.findById(product.getSupplier().getId())
                    .orElseThrow(() -> {
                        logger.error("Nhà cung cấp không tồn tại với ID: {}", product.getSupplier().getId());
                        return new IllegalArgumentException("Nhà cung cấp không tồn tại");
                    }));

            // Lưu sản phẩm
            productRepository.save(existingProduct);
            logger.info("Cập nhật sản phẩm thành công: ID {}", product.getId());
            return true;
        } catch (IllegalArgumentException e) {
            logger.error("Lỗi dữ liệu khi cập nhật sản phẩm ID {}: {}", product.getId(), e.getMessage());
            return false;
        } catch (Exception e) {
            logger.error("Lỗi không xác định khi cập nhật sản phẩm ID {}: {}", product.getId(), e.getMessage(), e);
            return false;
        }
    }

    @Override
    public Boolean delete(Long id) {
        try {
            logger.info("Xóa sản phẩm với ID: {}", id);
            productRepository.deleteById(id);
            logger.info("Xóa sản phẩm thành công: ID {}", id);
            return true;
        } catch (Exception e) {
            logger.error("Lỗi khi xóa sản phẩm ID {}: {}", id, e.getMessage(), e);
            return false;
        }
    }

    @Override
    public List<Product> getNewActiveProductsLimitedToTen() {
        logger.info("Lấy danh sách 10 sản phẩm mới và đang hoạt động");
        return this.productRepository.findAll().stream()
                .filter(product -> product.getPronewbook() != null && product.getPronewbook()
                        && product.getProstatus() != null && product.getProstatus())
                .limit(10)
                .collect(Collectors.toList());
    }

    @Override
    public List<Product> getProductsByCategoryId(Long categoryId) {
        logger.info("Lấy danh sách sản phẩm theo danh mục ID: {}", categoryId);
        return this.productRepository.findAll().stream()
                .filter(product -> product.getCategory() != null && product.getCategory().getId().equals(categoryId)
                        && product.getProstatus() != null && product.getProstatus())
                .collect(Collectors.toList());
    }

    @Override
    public List<Product> getProductsOnSale() {
        logger.info("Lấy danh sách sản phẩm đang giảm giá");
        return this.productRepository.findAll().stream()
                .filter(product -> product.getProsale() != null && product.getProsale() > 0)
                .collect(Collectors.toList());
    }

    @Override
    public List<Product> getNewBooks() {
        logger.info("Lấy danh sách sách mới");
        return this.productRepository.findAll().stream()
                .filter(product -> product.getPronewbook() != null && product.getPronewbook())
                .collect(Collectors.toList());
    }

    @Override
    public List<Product> getActiveProducts() {
        logger.info("Lấy danh sách 5 sản phẩm đang hoạt động");
        return this.productRepository.findAll().stream()
                .filter(product -> product.getProstatus() != null && product.getProstatus())
                .limit(5)
                .collect(Collectors.toList());
    }

    private void validateProduct(Product product) {
        if (product.getProname() == null || product.getProname().trim().isEmpty()) {
            logger.error("Tên sản phẩm không được để trống");
            throw new IllegalArgumentException("Tên sản phẩm không được để trống");
        }
        if (product.getProcontent() != null && product.getProcontent().length() > 4000) {
            logger.error("Nội dung sản phẩm vượt quá 4000 ký tự");
            throw new IllegalArgumentException("Nội dung sản phẩm vượt quá 4000 ký tự");
        }
        if (product.getCategory() == null || product.getCategory().getId() == null) {
            logger.error("Danh mục không được để trống");
            throw new IllegalArgumentException("Danh mục không được để trống");
        }
        if (product.getAuthor() == null || product.getAuthor().getId() == null) {
            logger.error("Tác giả không được để trống");
            throw new IllegalArgumentException("Tác giả không được để trống");
        }
        if (product.getSupplier() == null || product.getSupplier().getId() == null) {
            logger.error("Nhà cung cấp không được để trống");
            throw new IllegalArgumentException("Nhà cung cấp không được để trống");
        }
    }
}