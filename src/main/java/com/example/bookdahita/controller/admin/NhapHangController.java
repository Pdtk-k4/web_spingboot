package com.example.bookdahita.controller.admin;

import com.example.bookdahita.models.*;
import com.example.bookdahita.repository.HDNhapHangChiTietRepository;
import com.example.bookdahita.repository.HDNhapHangRepository;
import com.example.bookdahita.repository.InventoryRepository;
import com.example.bookdahita.repository.ProductRepository;
import com.example.bookdahita.repository.UserRepository;
import com.example.bookdahita.service.AuthorService;
import com.example.bookdahita.service.CategoryService;
import com.example.bookdahita.service.StorageService;
import com.example.bookdahita.service.SupplierService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin")
public class NhapHangController {

    private static final Logger logger = LoggerFactory.getLogger(NhapHangController.class);

    private static final String CHARACTERS = "abcdefghijklmnopqrstuvwxyz0123456789";
    private static final Random RANDOM = new Random();

    private String generateShortRandomString() {
        StringBuilder sb = new StringBuilder(8);
        for (int i = 0; i < 8; i++) {
            sb.append(CHARACTERS.charAt(RANDOM.nextInt(CHARACTERS.length())));
        }
        return sb.toString();
    }

    @Autowired
    private StorageService storageService;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private AuthorService authorService;

    @Autowired
    private SupplierService supplierService;

    @Autowired
    private HDNhapHangRepository hdNhapHangRepository;

    @Autowired
    private HDNhapHangChiTietRepository hdNhapHangChiTietRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private InventoryRepository inventoryRepository;

    @GetMapping("/nhaphang")
    public String nhaphang(Model model) {
        String maPhieu;
        Optional<HDNhapHang> pendingNhapHang = hdNhapHangRepository.findFirstByStatus("PENDING");
        if (pendingNhapHang.isPresent()) {
            maPhieu = pendingNhapHang.get().getId();
        } else {
            do {
                maPhieu = "PNH" + generateShortRandomString().toUpperCase();
            } while (hdNhapHangRepository.existsById(maPhieu));
        }

        model.addAttribute("maPhieu", maPhieu);
        model.addAttribute("nhapHangForm", new HDNhapHangChiTiet());
        model.addAttribute("listSupplier", supplierService.getAll());
        model.addAttribute("listCategory", categoryService.getAll());
        model.addAttribute("listAuthor", authorService.getAll());
        model.addAttribute("nhapHangChiTiets", hdNhapHangChiTietRepository.findByHdNhapHangId(maPhieu));
        return "admin/nhaphang";
    }

    @PostMapping("/nhaphang")
    @ResponseBody
    @Transactional
    public ResponseEntity<Map<String, Object>> saveNhapHang(
            @ModelAttribute("nhapHangForm") HDNhapHangChiTiet nhapHangChiTiet,
            @RequestParam(value = "coverImage", required = false) MultipartFile coverImage,
            @RequestParam("maPhieu") String maPhieu,
            @RequestParam(value = "product.id", required = false) String productId,
            @RequestParam(value = "product.proname", required = false) String proname,
            @RequestParam(value = "product.supplier.id", required = false) Long supplierId,
            @RequestParam(value = "product.category.id", required = false) Long categoryId,
            @RequestParam(value = "product.author.id", required = false) Long authorId) {
        Map<String, Object> response = new HashMap<>();

        try {
            // Log để kiểm tra coverImage
            logger.info("CoverImage is null: {}", coverImage == null);
            if (coverImage != null) {
                logger.info("CoverImage name: {}, size: {}", coverImage.getOriginalFilename(), coverImage.getSize());
            }

            // Lấy thông tin người dùng hiện tại
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            if (username == null || "anonymousUser".equals(username)) {
                throw new IllegalArgumentException("Người dùng chưa được xác thực");
            }

            User user = userRepository.findByUserName(username);
            if (user == null) {
                throw new IllegalArgumentException("Không tìm thấy người dùng: " + username);
            }

            // Kiểm tra hoặc tạo HDNhapHang
            HDNhapHang hdNhapHang = hdNhapHangRepository.findById(maPhieu)
                    .filter(nhapHang -> "PENDING".equals(nhapHang.getStatus()))
                    .orElseGet(() -> {
                        Optional<HDNhapHang> pendingNhapHang = hdNhapHangRepository.findFirstByStatus("PENDING");
                        if (pendingNhapHang.isPresent()) {
                            return pendingNhapHang.get();
                        } else {
                            return HDNhapHang.builder()
                                    .id(maPhieu)
                                    .createAt(LocalDateTime.now())
                                    .status("PENDING")
                                    .user(user)
                                    .build();
                        }
                    });
            hdNhapHangRepository.save(hdNhapHang);

            // Xử lý sản phẩm
            Product product;
            String fileName = "default.jpg"; // Giá trị mặc định cho ảnh bìa
            if (productId != null && !productId.trim().isEmpty()) {
                // Trường hợp sản phẩm đã tồn tại
                try {
                    Long id = Long.parseLong(productId);
                    product = productRepository.findById(id)
                            .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy sản phẩm với ID: " + productId));
                    fileName = product.getProimage() != null ? product.getProimage() : "default.jpg";
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("ID sản phẩm không hợp lệ: " + productId);
                }
            } else {
                // Trường hợp sản phẩm mới
                if (proname == null || proname.trim().isEmpty()) {
                    throw new IllegalArgumentException("Tên sản phẩm không được để trống khi tạo sản phẩm mới.");
                }
                if (supplierId == null) {
                    throw new IllegalArgumentException("Nhà cung cấp phải được cung cấp khi tạo sản phẩm mới.");
                }
                if (categoryId == null) {
                    throw new IllegalArgumentException("Thể loại phải được cung cấp khi tạo sản phẩm mới.");
                }
                if (authorId == null) {
                    throw new IllegalArgumentException("Tác giả phải được cung cấp khi tạo sản phẩm mới.");
                }

                // Tìm Supplier, Category, Author
                Supplier supplier = supplierService.findById(supplierId);
                if (supplier == null) {
                    throw new IllegalArgumentException("Nhà cung cấp không tồn tại: " + supplierId);
                }
                Category category = categoryService.findById(categoryId);
                if (category == null) {
                    throw new IllegalArgumentException("Thể loại không tồn tại: " + categoryId);
                }
                Author author = authorService.findById(authorId);
                if (author == null) {
                    throw new IllegalArgumentException("Tác giả không tồn tại: " + authorId);
                }

                // Tạo sản phẩm mới với các giá trị mặc định
                product = Product.builder()
                        .proname(proname)
                        .supplier(supplier)
                        .category(category)
                        .author(author)
                        .proimage("default.jpg") // Sẽ được cập nhật sau nếu có ảnh
                        .proprice(10000) // Giá trị mặc định
                        .procontent("") // Giá trị mặc định
                        .prosale(0) // Giá trị mặc định
                        .pronewbook(true) // Giá trị mặc định
                        .prostatus(true) // Giá trị mặc định
                        .build();
                product = productRepository.save(product);
            }

            // Xử lý ảnh bìa
            if (coverImage != null && !coverImage.isEmpty()) {
                String originalFileName = coverImage.getOriginalFilename();
                logger.info("Processing coverImage: {}", originalFileName);
                if (originalFileName == null || originalFileName.trim().isEmpty()) {
                    throw new IllegalArgumentException("Tên file ảnh bìa không hợp lệ");
                }
                String extension = originalFileName.substring(originalFileName.lastIndexOf(".")).toLowerCase();
                fileName = generateShortRandomString() + extension;
                storageService.store(coverImage, fileName);
                logger.info("Stored coverImage: {}", fileName);
                product.setProimage(fileName);
                productRepository.save(product);
            } else {
                logger.warn("No coverImage provided or coverImage is empty, using default: {}", fileName);
            }

            // Gán ảnh bìa cho HDNhapHangChiTiet
            nhapHangChiTiet.setProimage(fileName);

            // Gán thông tin vào nhapHangChiTiet
            nhapHangChiTiet.setProduct(product);
            nhapHangChiTiet.setSupplier(product.getSupplier());
            nhapHangChiTiet.setCategory(product.getCategory());
            nhapHangChiTiet.setAuthor(product.getAuthor());
            nhapHangChiTiet.setHdNhapHang(hdNhapHang);
            hdNhapHangChiTietRepository.save(nhapHangChiTiet);

            // Cập nhật tồn kho
            Inventory inventory = inventoryRepository.findByProduct(nhapHangChiTiet.getProduct())
                    .orElse(Inventory.builder()
                            .product(nhapHangChiTiet.getProduct())
                            .quantity(0)
                            .lastUpdated(LocalDateTime.now())
                            .build());
            inventory.setQuantity(inventory.getQuantity() + nhapHangChiTiet.getQuantity());
            inventory.setLastUpdated(LocalDateTime.now());
            inventoryRepository.save(inventory);

            // Trả về danh sách chi tiết nhập hàng
            List<HDNhapHangChiTiet> nhapHangChiTiets = hdNhapHangChiTietRepository.findByHdNhapHangId(hdNhapHang.getId());
            List<Map<String, Object>> formattedResults = nhapHangChiTiets.stream().map(item -> {
                Map<String, Object> result = new HashMap<>();
                result.put("id", item.getId());
                result.put("proimage", item.getProimage() != null ? item.getProimage() : "default.jpg");
                result.put("proname", item.getProduct() != null ? item.getProduct().getProname() : "N/A");
                result.put("importPrice", item.getImportPrice());
                result.put("quantity", item.getQuantity());
                result.put("totalPrice", item.getImportPrice() * item.getQuantity());
                result.put("idsup", item.getSupplier() != null ? item.getSupplier().getId() : null);
                result.put("idcat", item.getCategory() != null ? item.getCategory().getId() : null);
                result.put("idau", item.getAuthor() != null ? item.getAuthor().getId() : null);
                return result;
            }).collect(Collectors.toList());

            response.put("message", "Thêm phiếu nhập thành công!");
            response.put("nhapHangChiTiets", formattedResults);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            logger.error("Lỗi dữ liệu: {}", e.getMessage());
            response.put("error", "Lỗi dữ liệu: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (Exception e) {
            logger.error("Lỗi khi lưu phiếu nhập hoặc tệp: {}", e.getMessage(), e);
            response.put("error", "Lỗi khi lưu phiếu nhập hoặc tệp: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping("/nhaphang/save")
    @ResponseBody
    @Transactional
    public ResponseEntity<Map<String, Object>> saveNhapHangStatus(@RequestParam("maPhieu") String maPhieu) {
        Map<String, Object> response = new HashMap<>();

        try {
            // Kiểm tra mã phiếu
            if (maPhieu == null || maPhieu.trim().isEmpty()) {
                throw new IllegalArgumentException("Mã phiếu nhập không được để trống");
            }

            // Tìm phiếu nhập
            HDNhapHang hdNhapHang = hdNhapHangRepository.findById(maPhieu)
                    .filter(nhapHang -> "PENDING".equals(nhapHang.getStatus()))
                    .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy phiếu nhập với mã: " + maPhieu + " hoặc phiếu đã được lưu"));

            // Cập nhật trạng thái
            hdNhapHang.setStatus("COMPLETED");
            hdNhapHang.setCreateAt(LocalDateTime.now()); // Cập nhật thời gian hoàn thành
            hdNhapHangRepository.save(hdNhapHang);

            logger.info("Cập nhật trạng thái phiếu nhập {} thành COMPLETED", maPhieu);
            response.put("message", "Lưu phiếu nhập thành công!");
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            logger.error("Lỗi dữ liệu: {}", e.getMessage());
            response.put("error", "Lỗi dữ liệu: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (Exception e) {
            logger.error("Lỗi khi lưu trạng thái phiếu nhập: {}", e.getMessage(), e);
            response.put("error", "Lỗi khi lưu trạng thái phiếu nhập: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/search")
    @ResponseBody
    public Map<String, Object> searchProducts(@RequestParam("query") String query) {
        List<Product> products = productRepository.searchByNameOrCategory(query)
                .stream()
                .limit(5)
                .collect(Collectors.toList());

        List<Map<String, Object>> formattedResults = products.stream().map(product -> {
            Map<String, Object> result = new HashMap<>();
            result.put("id", product.getId());
            result.put("proname", product.getProname());
            result.put("proimage", product.getProimage() != null ? product.getProimage() : "default.jpg");
            result.put("catname", product.getCategory() != null ? product.getCategory().getCatname() : "");
            result.put("categoryId", product.getCategory() != null ? product.getCategory().getId() : null);
            result.put("supname", product.getSupplier() != null ? product.getSupplier().getSupname() : "");
            result.put("supplierId", product.getSupplier() != null ? product.getSupplier().getId() : null);
            result.put("auname", product.getAuthor() != null ? product.getAuthor().getAuname() : "");
            result.put("authorId", product.getAuthor() != null ? product.getAuthor().getId() : null);
            return result;
        }).collect(Collectors.toList());

        Map<String, Object> response = new HashMap<>();
        response.put("results", formattedResults);
        response.put("total", products.size());
        return response;
    }
}