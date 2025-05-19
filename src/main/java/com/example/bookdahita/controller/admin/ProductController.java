package com.example.bookdahita.controller.admin;

import com.example.bookdahita.models.Category;
import com.example.bookdahita.models.Product;
import com.example.bookdahita.models.ProductsImages;
import com.example.bookdahita.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Random;

@Controller
@RequestMapping("/admin")
public class ProductController {

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
            if (originalFileName == null || originalFileName.trim().isEmpty()) {
                throw new IllegalArgumentException("Tên file ảnh bìa không hợp lệ");
            }
            String extension = originalFileName.substring(originalFileName.lastIndexOf(".")).toLowerCase();
            String fileName = generateShortRandomString() + extension;
            storageService.store(fileImage, fileName);
            product.setProimage(fileName);
            System.out.println("Lưu ảnh bìa: " + fileName);

            // Lưu sản phẩm
            if (!productService.create(product)) {
                throw new RuntimeException("Lỗi khi lưu sản phẩm");
            }

            // Xử lý ảnh chi tiết (proGallery)
            if (proGallery != null && proGallery.length > 0) {
                for (MultipartFile file : proGallery) {
                    if (!file.isEmpty()) {
                        originalFileName = file.getOriginalFilename();
                        if (originalFileName == null || originalFileName.trim().isEmpty()) {
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
                        System.out.println("Lưu ảnh chi tiết: " + fileName);
                    }
                }
            }

            redirectAttributes.addFlashAttribute("success", true);
            return "redirect:/admin/productadd";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", "Lỗi dữ liệu: " + e.getMessage());
            model.addAttribute("product", product);
            model.addAttribute("listCategory", categoryService.getAll());
            model.addAttribute("listAuthor", authorService.getAll());
            model.addAttribute("listSupplier", supplierService.getAll());
            return "admin/productadd";
        } catch (RuntimeException e) {
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
            model.addAttribute("error", "Sản phẩm không tồn tại");
            return "redirect:/admin/productlist";
        }
        System.out.println("Product ID: " + id);
        System.out.println("pronewbook: " + product.getPronewbook());
        System.out.println("prostatus: " + product.getProstatus());
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
                redirectAttributes.addFlashAttribute("error", "Sản phẩm không tồn tại");
                return "redirect:/admin/productlist";
            }

            // Gán ID cho sản phẩm
            product.setId(id);

            // Gán giá trị mặc định nếu null
            if (product.getPronewbook() == null) {
                product.setPronewbook(false);
                System.out.println("Set pronewbook to false");
            } else {
                System.out.println("Set pronewbook to: " + product.getPronewbook());
            }
            if (product.getProstatus() == null) {
                product.setProstatus(false);
                System.out.println("Set prostatus to false");
            } else {
                System.out.println("Set prostatus to: " + product.getProstatus());
            }
            if (product.getProprice() == null) {
                product.setProprice(0);
                System.out.println("Set proprice to 0");
            } else {
                System.out.println("Set proprice to: " + product.getProprice());
            }
            if (product.getProsale() == null) {
                product.setProsale(0);
                System.out.println("Set prosale to 0");
            } else {
                System.out.println("Set prosale to: " + product.getProsale());
            }

            // Xử lý ảnh bìa
            if (fileImage != null && !fileImage.isEmpty()) {
                // Xóa ảnh bìa cũ nếu tồn tại
                if (existingProduct.getProimage() != null && !existingProduct.getProimage().isEmpty()) {
                    storageService.delete(existingProduct.getProimage());
                    System.out.println("Đã xóa ảnh bìa cũ: " + existingProduct.getProimage());
                }
                // Lưu ảnh bìa mới
                String originalFileName = fileImage.getOriginalFilename();
                if (originalFileName == null || originalFileName.trim().isEmpty()) {
                    throw new IllegalArgumentException("Tên file ảnh bìa không hợp lệ");
                }
                String extension = originalFileName.substring(originalFileName.lastIndexOf(".")).toLowerCase();
                String fileName = generateShortRandomString() + extension;
                storageService.store(fileImage, fileName);
                product.setProimage(fileName);
                System.out.println("Set proimage to: " + fileName);
            } else {
                // Giữ ảnh bìa hiện tại nếu không upload ảnh mới
                product.setProimage(existingProduct.getProimage());
                System.out.println("Kept existing proimage: " + existingProduct.getProimage());
            }

            // Xử lý ảnh chi tiết
            boolean hasNewGalleryImages = false;
            if (proGallery != null && proGallery.length > 0) {
                // Kiểm tra xem có ít nhất một file hợp lệ trong proGallery
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
                    if (img.getPiimage() != null) {
                        storageService.delete(img.getPiimage());
                        System.out.println("Đã xóa ảnh chi tiết cũ: " + img.getPiimage());
                    }
                }
                productImageService.deleteByProductId(id);
                System.out.println("Đã xóa bản ghi ảnh chi tiết cũ cho product ID: " + id);

                // Lưu ảnh chi tiết mới
                for (MultipartFile file : proGallery) {
                    if (!file.isEmpty()) {
                        String originalFileName = file.getOriginalFilename();
                        if (originalFileName == null || originalFileName.trim().isEmpty()) {
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
                        System.out.println("Saved product image (gallery): " + fileName);
                    }
                }
            }

            // Cập nhật sản phẩm
            if (productService.update(product)) {
                redirectAttributes.addFlashAttribute("success", true);
                return "redirect:/admin/productlist";
            } else {
                model.addAttribute("error", "Cập nhật sản phẩm thất bại!");
                model.addAttribute("product", product);
                model.addAttribute("listCategory", categoryService.getAll());
                model.addAttribute("listAuthor", authorService.getAll());
                model.addAttribute("listSupplier", supplierService.getAll());
                return "admin/productedit";
            }
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", "Lỗi dữ liệu: " + e.getMessage());
            model.addAttribute("product", product);
            model.addAttribute("listCategory", categoryService.getAll());
            model.addAttribute("listAuthor", authorService.getAll());
            model.addAttribute("listSupplier", supplierService.getAll());
            return "admin/productedit";
        } catch (RuntimeException e) {
            model.addAttribute("error", "Lỗi khi lưu tệp hoặc sản phẩm: " + e.getMessage());
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
                redirectAttributes.addFlashAttribute("error", "Sản phẩm không tồn tại");
                return "redirect:/admin/productlist";
            }

            // Xóa ảnh bìa từ thư mục uploads
            if (product.getProimage() != null && !product.getProimage().isEmpty()) {
                storageService.delete(product.getProimage());
                System.out.println("Đã xóa ảnh bìa: " + product.getProimage());
            }

            // Xóa ảnh chi tiết từ thư mục uploads và bảng ProductsImages
            List<ProductsImages> productImages = productImageService.getAll().stream()
                    .filter(img -> img.getProduct() != null && img.getProduct().getId().equals(id))
                    .toList();
            for (ProductsImages img : productImages) {
                if (img.getPiimage() != null && !img.getPiimage().isEmpty()) {
                    storageService.delete(img.getPiimage());
                    System.out.println("Đã xóa ảnh chi tiết: " + img.getPiimage());
                }
            }
            productImageService.deleteByProductId(id);
            System.out.println("Đã xóa bản ghi ảnh chi tiết cho product ID: " + id);

            // Xóa sản phẩm
            if (productService.delete(id)) {
                redirectAttributes.addFlashAttribute("success", true);
                System.out.println("Đã xóa sản phẩm ID: " + id);
            } else {
                redirectAttributes.addFlashAttribute("error", "Xóa sản phẩm thất bại");
                System.out.println("Không thể xóa sản phẩm ID: " + id);
            }
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi khi xóa sản phẩm: " + e.getMessage());
            System.out.println("Lỗi khi xóa sản phẩm ID: " + id + ", thông báo: " + e.getMessage());
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
}