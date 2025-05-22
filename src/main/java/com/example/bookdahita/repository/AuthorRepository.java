package com.example.bookdahita.repository;

import com.example.bookdahita.models.Author;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthorRepository extends JpaRepository<Author,Long> {
}
