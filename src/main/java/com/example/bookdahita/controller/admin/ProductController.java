package com.example.bookdahita.controller.admin;

import com.example.bookdahita.models.*;
import com.example.bookdahita.repository.HDNhapHangChiTietRepository;
import com.example.bookdahita.repository.InventoryRepository;
import com.example.bookdahita.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.*;

@Controller
@RequestMapping("/admin")
public class ProductController {

    private static final Logger logger = LoggerFactory.getLogger(ProductController.class);

    @Autowired
    private StorageService storageService;

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductImageService productImageService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private AuthorService authorService;

    @Autowired
    private SupplierService supplierService;

    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private HDNhapHangChiTietRepository hdNhapHangChiTietRepository;
    @Autowired
    private InventoryService inventoryService;

    // Hàm tạo chuỗi ngẫu nhiên 8 ký tự
    private static final String CHARACTERS = "abcdefghijklmnopqrstuvwxyz0123456789";
    private static final Random RANDOM = new Random();

    private String generateShortRandomString() {
        StringBuilder sb = new StringBuilder(8);
        for (int i = 0; i < 8; i++) {
            sb.append(CHARACTERS.charAt(RANDOM.nextInt(CHARACTERS.length())));
        }
        return sb.toString();
    }

    @GetMapping("/productlist")
    public String productList(Model model) {
        List<Product> products = productService.getAll();
        model.addAttribute("products", products);
        return "admin/productlist";
    }

    @GetMapping("/productadd")
    public String productadd(Model model) {
        Product product = new Product();
        product.setPronewbook(false);
        product.setProstatus(false);
        product.setProprice(0);
        product.setProsale(0);
        model.addAttribute("product", product);
        model.addAttribute("listCategory", categoryService.getAll());
        model.addAttribute("listAuthor", authorService.getAll());
        model.addAttribute("listSupplier", supplierService.getAll());
        return "admin/productadd";
    }

    @PostMapping("/productadd")
    public String save(@ModelAttribute("product") Product product,
                       @RequestParam("fileImage") MultipartFile fileImage,
                       @RequestParam(value = "proGallery", required = false) MultipartFile[] proGallery,
                       Model model,
                       RedirectAttributes redirectAttributes) {
        try {
            // Xử lý ảnh bìa
            if (fileImage == null || fileImage.isEmpty()) {
                throw new IllegalArgumentException("Ảnh bìa không được để trống");
            }
            String originalFileName = fileImage.getOriginalFilename();
            if (originalFileName == null || originalFileName.trim().isEmpty() || !originalFileName.contains(".")) {
                throw new IllegalArgumentException("Tên file ảnh bìa không hợp lệ");
            }
            String extension = originalFileName.substring(originalFileName.lastIndexOf(".")).toLowerCase();
            String fileName = generateShortRandomString() + extension;
            storageService.store(fileImage, fileName);
            product.setProimage(fileName);
            logger.info("Lưu ảnh bìa: {}", fileName);

            // Kiểm tra các thực thể liên quan
            if (product.getCategory() == null || product.getCategory().getId() == null ||
                    product.getAuthor() == null || product.getAuthor().getId() == null ||
                    product.getSupplier() == null || product.getSupplier().getId() == null) {
                throw new IllegalArgumentException("Danh mục, tác giả hoặc nhà cung cấp không hợp lệ");
            }

            // Lưu sản phẩm
            if (!productService.create(product)) {
                throw new RuntimeException("Lỗi khi lưu sản phẩm");
            }

            // Xử lý ảnh chi tiết (proGallery)
            if (proGallery != null && proGallery.length > 0) {
                for (MultipartFile file : proGallery) {
                    if (!file.isEmpty()) {
                        originalFileName = file.getOriginalFilename();
                        if (originalFileName == null || originalFileName.trim().isEmpty() || !originalFileName.contains(".")) {
                            throw new IllegalArgumentException("Tên file ảnh chi tiết không hợp lệ");
                        }
                        extension = originalFileName.substring(originalFileName.lastIndexOf(".")).toLowerCase();
                        fileName = generateShortRandomString() + extension;
                        storageService.store(file, fileName);

                        // Tạo bản ghi ProductsImages
                        ProductsImages productImage = new ProductsImages();
                        productImage.setPiimage(fileName);
                        productImage.setProduct(product);
                        productImageService.create(productImage);
                        logger.info("Lưu ảnh chi tiết: {}", fileName);
                    }
                }
            }

            redirectAttributes.addFlashAttribute("success", true);
            return "redirect:/admin/productadd";
        } catch (IllegalArgumentException e) {
            logger.error("Lỗi dữ liệu khi thêm sản phẩm: {}", e.getMessage());
            model.addAttribute("error", "Lỗi dữ liệu: " + e.getMessage());
            model.addAttribute("product", product);
            model.addAttribute("listCategory", categoryService.getAll());
            model.addAttribute("listAuthor", authorService.getAll());
            model.addAttribute("listSupplier", supplierService.getAll());
            return "admin/productadd";
        } catch (RuntimeException e) {
            logger.error("Lỗi khi lưu sản phẩm hoặc tệp: {}", e.getMessage());
            model.addAttribute("error", "Lỗi khi lưu sản phẩm hoặc tệp: " + e.getMessage());
            model.addAttribute("product", product);
            model.addAttribute("listCategory", categoryService.getAll());
            model.addAttribute("listAuthor", authorService.getAll());
            model.addAttribute("listSupplier", supplierService.getAll());
            return "admin/productadd";
        }
    }

    @GetMapping("/productedit/{id}")
    public String productedit(@PathVariable("id") Long id, Model model) {
        Product product = productService.findById(id);
        if (product == null) {
            logger.error("Sản phẩm không tồn tại với ID: {}", id);
            model.addAttribute("error", "Sản phẩm không tồn tại");
            return "redirect:/admin/productlist";
        }
        logger.info("Hiển thị form chỉnh sửa sản phẩm ID: {}, pronewbook: {}, prostatus: {}",
                id, product.getPronewbook(), product.getProstatus());
        model.addAttribute("product", product);
        model.addAttribute("listCategory", categoryService.getAll());
        model.addAttribute("listAuthor", authorService.getAll());
        model.addAttribute("listSupplier", supplierService.getAll());
        return "admin/productedit";
    }

    @PostMapping("/productedit/{id}")
    public String update(@PathVariable("id") Long id,
                         @ModelAttribute("product") Product product,
                         @RequestParam(value = "fileImage", required = false) MultipartFile fileImage,
                         @RequestParam(value = "proGallery", required = false) MultipartFile[] proGallery,
                         Model model,
                         RedirectAttributes redirectAttributes) {
        try {
            // Tìm sản phẩm hiện tại
            Product existingProduct = productService.findById(id);
            if (existingProduct == null) {
                logger.error("Sản phẩm không tồn tại với ID: {}", id);
                redirectAttributes.addFlashAttribute("error", "Sản phẩm không tồn tại");
                return "redirect:/admin/productlist";
            }

            // Gán ID cho sản phẩm
            product.setId(id);

            // Gán giá trị mặc định nếu null
            product.setPronewbook(product.getPronewbook() != null ? product.getPronewbook() : false);
            logger.info("Set pronewbook to: {}", product.getPronewbook());
            product.setProstatus(product.getProstatus() != null ? product.getProstatus() : false);
            logger.info("Set prostatus to: {}", product.getProstatus());
            product.setProprice(product.getProprice() != null ? product.getProprice() : 0);
            logger.info("Set proprice to: {}", product.getProprice());
            product.setProsale(product.getProsale() != null ? product.getProsale() : 0);
            logger.info("Set prosale to: {}", product.getProsale());

            // Kiểm tra các thực thể liên quan
            if (product.getCategory() == null || product.getCategory().getId() == null ||
                    product.getAuthor() == null || product.getAuthor().getId() == null ||
                    product.getSupplier() == null || product.getSupplier().getId() == null) {
                throw new IllegalArgumentException("Danh mục, tác giả hoặc nhà cung cấp không hợp lệ");
            }

            // Xử lý ảnh bìa
            if (fileImage != null && !fileImage.isEmpty()) {
                // Xóa ảnh bìa cũ nếu tồn tại
                if (existingProduct.getProimage() != null && !existingProduct.getProimage().isEmpty()) {
                    try {
                        storageService.delete(existingProduct.getProimage());
                        logger.info("Đã xóa ảnh bìa cũ: {}", existingProduct.getProimage());
                    } catch (Exception e) {
                        logger.warn("Không thể xóa ảnh bìa cũ: {}. Tiếp tục xử lý.", e.getMessage());
                    }
                }
                // Lưu ảnh bìa mới
                String originalFileName = fileImage.getOriginalFilename();
                if (originalFileName == null || originalFileName.trim().isEmpty() || !originalFileName.contains(".")) {
                    throw new IllegalArgumentException("Tên file ảnh bìa không hợp lệ");
                }
                String extension = originalFileName.substring(originalFileName.lastIndexOf(".")).toLowerCase();
                String fileName = generateShortRandomString() + extension;
                storageService.store(fileImage, fileName);
                product.setProimage(fileName);
                logger.info("Set proimage to: {}", fileName);
            } else {
                // Giữ ảnh bìa hiện tại nếu không upload ảnh mới
                product.setProimage(existingProduct.getProimage());
                logger.info("Kept existing proimage: {}", existingProduct.getProimage());
            }

            // Xử lý ảnh chi tiết
            boolean hasNewGalleryImages = false;
            if (proGallery != null && proGallery.length > 0) {
                for (MultipartFile file : proGallery) {
                    if (!file.isEmpty()) {
                        hasNewGalleryImages = true;
                        break;
                    }
                }
            }

            if (hasNewGalleryImages) {
                // Xóa ảnh chi tiết cũ trong cơ sở dữ liệu và thư mục uploads
                List<ProductsImages> existingImages = productImageService.getAll().stream()
                        .filter(img -> img.getProduct() != null && img.getProduct().getId().equals(id))
                        .toList();
                for (ProductsImages img : existingImages) {
                    if (img.getPiimage() != null && !img.getPiimage().isEmpty()) {
                        try {
                            storageService.delete(img.getPiimage());
                            logger.info("Đã xóa ảnh chi tiết cũ: {}", img.getPiimage());
                        } catch (Exception e) {
                            logger.warn("Không thể xóa ảnh chi tiết cũ: {}. Tiếp tục xử lý.", e.getMessage());
                        }
                    }
                }
                productImageService.deleteByProductId(id);
                logger.info("Đã xóa bản ghi ảnh chi tiết cũ cho product ID: {}", id);

                // Lưu ảnh chi tiết mới
                for (MultipartFile file : proGallery) {
                    if (!file.isEmpty()) {
                        String originalFileName = file.getOriginalFilename();
                        if (originalFileName == null || originalFileName.trim().isEmpty() || !originalFileName.contains(".")) {
                            throw new IllegalArgumentException("Tên file ảnh chi tiết không hợp lệ");
                        }
                        String extension = originalFileName.substring(originalFileName.lastIndexOf(".")).toLowerCase();
                        String fileName = generateShortRandomString() + extension;
                        storageService.store(file, fileName);
                        // Lưu vào tbl_products_images
                        ProductsImages productImage = new ProductsImages();
                        productImage.setPiimage(fileName);
                        productImage.setProduct(product);
                        productImageService.create(productImage);
                        logger.info("Saved product image (gallery): {}", fileName);
                    }
                }
            }

            // Cập nhật sản phẩm
            if (productService.update(product)) {
                logger.info("Cập nhật sản phẩm thành công: ID {}", id);
                redirectAttributes.addFlashAttribute("success", true);
                return "redirect:/admin/productlist";
            } else {
                logger.error("Cập nhật sản phẩm thất bại: ID {}", id);
                model.addAttribute("error", "Cập nhật sản phẩm thất bại! Vui lòng kiểm tra dữ liệu và thử lại.");
                model.addAttribute("product", product);
                model.addAttribute("listCategory", categoryService.getAll());
                model.addAttribute("listAuthor", authorService.getAll());
                model.addAttribute("listSupplier", supplierService.getAll());
                return "admin/productedit";
            }
        } catch (IllegalArgumentException e) {
            logger.error("Lỗi dữ liệu khi cập nhật sản phẩm ID {}: {}", id, e.getMessage());
            model.addAttribute("error", "Lỗi dữ liệu: " + e.getMessage());
            model.addAttribute("product", product);
            model.addAttribute("listCategory", categoryService.getAll());
            model.addAttribute("listAuthor", authorService.getAll());
            model.addAttribute("listSupplier", supplierService.getAll());
            return "admin/productedit";
        } catch (Exception e) {
            logger.error("Lỗi không xác định khi cập nhật sản phẩm ID {}: {}", id, e.getMessage(), e);
            model.addAttribute("error", "Lỗi khi lưu sản phẩm hoặc tệp: " + e.getMessage());
            model.addAttribute("product", product);
            model.addAttribute("listCategory", categoryService.getAll());
            model.addAttribute("listAuthor", authorService.getAll());
            model.addAttribute("listSupplier", supplierService.getAll());
            return "admin/productedit";
        }
    }

    @GetMapping("/productdel/{id}")
    public String productdel(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            // Kiểm tra sản phẩm tồn tại
            Product product = productService.findById(id);
            if (product == null) {
                logger.error("Sản phẩm không tồn tại với ID: {}", id);
                redirectAttributes.addFlashAttribute("error", "Sản phẩm không tồn tại");
                return "redirect:/admin/productlist";
            }

            // Xóa sản phẩm
            if (productService.delete(id)) {
                // Chỉ xóa ảnh bìa và ảnh chi tiết khi sản phẩm được xóa thành công
                if (product.getProimage() != null && !product.getProimage().isEmpty()) {
                    try {
                        storageService.delete(product.getProimage());
                        logger.info("Đã xóa ảnh bìa: {}", product.getProimage());
                    } catch (Exception e) {
                        logger.warn("Không thể xóa ảnh bìa: {}. Tiếp tục xử lý.", e.getMessage());
                    }
                }

                // Xóa ảnh chi tiết từ thư mục uploads và bảng ProductsImages
                List<ProductsImages> productImages = productImageService.getAll().stream()
                        .filter(img -> img.getProduct() != null && img.getProduct().getId().equals(id))
                        .toList();
                for (ProductsImages img : productImages) {
                    if (img.getPiimage() != null && !img.getPiimage().isEmpty()) {
                        try {
                            storageService.delete(img.getPiimage());
                            logger.info("Đã xóa ảnh chi tiết: {}", img.getPiimage());
                        } catch (Exception e) {
                            logger.warn("Không thể xóa ảnh chi tiết: {}. Tiếp tục xử lý.", e.getMessage());
                        }
                    }
                }
                productImageService.deleteByProductId(id);
                logger.info("Đã xóa bản ghi ảnh chi tiết cho product ID: {}", id);

                redirectAttributes.addFlashAttribute("success", true);
                logger.info("Đã xóa sản phẩm ID: {}", id);
            } else {
                logger.error("Không thể xóa sản phẩm ID: {}", id);
                redirectAttributes.addFlashAttribute("error", "Không thể xóa sản phẩm");
            }
        } catch (Exception e) {
            logger.error("Lỗi khi xóa sản phẩm ID {}: {}", id, e.getMessage(), e);
            redirectAttributes.addFlashAttribute("error", "Không thể xóa sản phẩm: " + e.getMessage());
        }
        return "redirect:/admin/productlist";
    }

    @GetMapping("/proSale")
    public String proSale(Model model) {
        List<Product> saleProducts = productService.getProductsOnSale();
        model.addAttribute("saleProducts", saleProducts);
        return "admin/proSale";
    }

    @GetMapping("/proNewBook")
    public String proNewBook(Model model) {
        List<Product> newBooks = productService.getNewBooks();
        model.addAttribute("newBooks", newBooks);
        return "admin/proNewBook";
    }

    @GetMapping("/tonkho")
    public String tonkho(Model model) {
        List<Inventory> inventories = inventoryRepository.findAll();
        Map<Long, Integer> importPriceMap = new HashMap<>();
        for (Inventory inventory : inventories) {
            Long productId = inventory.getProduct().getId();
            Optional<HDNhapHangChiTiet> latestImport = hdNhapHangChiTietRepository.findTopByProductIdOrderByIdDesc(productId);
            importPriceMap.put(productId, latestImport.map(HDNhapHangChiTiet::getImportPrice).orElse(0));
        }
        model.addAttribute("inventories", inventories);
        model.addAttribute("importPriceMap", importPriceMap);
        return "admin/tonkho";
    }

    @GetMapping("/editTonKho/{id}")
    public String editTonKho(@PathVariable("id") Long id, Model model) {
        Optional<Inventory> inventoryOptional = inventoryRepository.findById(id);
        if (inventoryOptional.isEmpty()) {
            model.addAttribute("error", "Tồn kho không tồn tại");
            return "redirect:/admin/tonkho";
        }
        Inventory inventories = inventoryOptional.get();
        model.addAttribute("inventories", inventories);
        return "admin/editTonKho";
    }

    @PostMapping("/editTonKho/{id}")
    public String updateTonKho(@PathVariable("id") Long id, @ModelAttribute("inventories") Inventory inventories, Model model) {
        Optional<Inventory> inventoryOptional = inventoryRepository.findById(id);
        if (inventoryOptional.isEmpty()) {
            model.addAttribute("error", "Tồn kho không tồn tại");
            return "redirect:/admin/tonkho";
        }
        Inventory existingInventory = inventoryOptional.get();
        if (inventories.getQuantity() < 0) {
            model.addAttribute("error", "Số lượng không được âm!");
            model.addAttribute("inventories", existingInventory);
            return "admin/editTonKho";
        }
        existingInventory.setQuantity(inventories.getQuantity());
        existingInventory.setLastUpdated(LocalDateTime.now());
        if (inventoryService.update(existingInventory)) {
            model.addAttribute("success", "Cập nhật thành công!");
            return "redirect:/admin/tonkho";
        } else {
            model.addAttribute("error", "Cập nhật thất bại!");
            model.addAttribute("inventories", existingInventory);
            return "admin/editTonKho";
        }
    }
}