package com.example.bookdahita.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects;

@Service
public class FileSystemStorageService implements StorageService {

    private final Path rootLocation;

    public FileSystemStorageService() {
        this.rootLocation = Paths.get("src/main/resources/static/resources/uploads");
    }

    @Override
    public void init() {
        try {
            Files.createDirectories(rootLocation);
        } catch (IOException e) {
            throw new RuntimeException("Không thể tạo thư mục lưu trữ: " + rootLocation, e);
        }
    }

    @Override
    public void store(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File không được null hoặc rỗng");
        }
        store(file, file.getOriginalFilename());
    }

    public void store(MultipartFile file, String fileName) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File không được null hoặc rỗng");
        }
        if (fileName == null || fileName.trim().isEmpty()) {
            throw new IllegalArgumentException("Tên file không được null hoặc rỗng");
        }
        try {
            // Đảm bảo tên file hợp lệ (loại bỏ ký tự không mong muốn)
            String sanitizedFileName = fileName.replaceAll("[^a-zA-Z0-9._-]", "_");
            Path destinationFile = this.rootLocation.resolve(Paths.get(sanitizedFileName)).normalize().toAbsolutePath();

            // Kiểm tra xem destinationFile có nằm trong rootLocation không
            if (!destinationFile.startsWith(rootLocation.toAbsolutePath())) {
                throw new SecurityException("Không thể lưu file ngoài thư mục cho phép");
            }

            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, destinationFile, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException e) {
            throw new RuntimeException("Lỗi khi lưu tệp: " + fileName, e);
        }
    }


    @Override
    public void delete(String fileName) {
        if (fileName == null || fileName.trim().isEmpty()) {
            return; // Không xóa nếu tên file không hợp lệ
        }
        try {
            Path filePath = this.rootLocation.resolve(fileName).normalize().toAbsolutePath();
            // Kiểm tra xem file có nằm trong rootLocation không
            if (!filePath.startsWith(rootLocation.toAbsolutePath())) {
                throw new SecurityException("Không thể xóa file ngoài thư mục cho phép");
            }
            Files.deleteIfExists(filePath);
            System.out.println("Đã xóa file: " + fileName);
        } catch (IOException e) {
            throw new RuntimeException("Lỗi khi xóa tệp: " + fileName, e);
        }
    }
}