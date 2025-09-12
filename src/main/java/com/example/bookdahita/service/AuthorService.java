package com.example.bookdahita.service;

import com.example.bookdahita.models.Author;

import java.util.List;

public interface AuthorService {
    List<Author> getAll();
    Boolean create(Author author);
    Author findById(Long id);
    Boolean update(Author author);
    Boolean delete(Long id);
}