package com.example.bookdahita.service;

import com.example.bookdahita.models.Category;

import java.util.List;

public interface CategoryService {
    List<Category> getAll();
    List<Category> getAllByOrderByIdDesc();
    Boolean create(Category category);
    Category findById(Long id);
    Boolean update(Category category);
    Boolean delete(Long id);
    List<Category> getActiveCategoriesLimitedToTen();
    List<Category> getActiveCategoriesLimitedToFour();
}
