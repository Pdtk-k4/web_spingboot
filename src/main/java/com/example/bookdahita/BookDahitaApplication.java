package com.example.bookdahita;

import com.example.bookdahita.service.StorageService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class BookDahitaApplication {

    public static void main(String[] args) {
        SpringApplication.run(BookDahitaApplication.class, args);
    }

    @Bean
    CommandLineRunner init(StorageService storageService) {
        return args -> {
            storageService.init();
        };
    }

}
