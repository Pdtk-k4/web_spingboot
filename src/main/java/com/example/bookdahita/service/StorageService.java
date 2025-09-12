package com.example.bookdahita.service;

import org.springframework.web.multipart.MultipartFile;

public interface StorageService {
    void init();
    void store(MultipartFile file);
    void store(MultipartFile file, String fileName);
    void delete(String fileName);
}