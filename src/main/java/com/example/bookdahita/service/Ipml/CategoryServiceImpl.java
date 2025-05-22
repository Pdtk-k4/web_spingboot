package com.example.bookdahita.service.Ipml;

import com.example.bookdahita.models.Category;
import com.example.bookdahita.repository.CategoryRepository;
import com.example.bookdahita.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Override
    public List<Category> getAll() {
        return this.categoryRepository.findAll();
    }

    @Override
    public List<Category> getAllByOrderByIdDesc() {
        return this.categoryRepository.findAllByOrderByIdDesc();
    }

    @Override
    public Boolean create(Category category) {
        try {
            this.categoryRepository.save(category);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public Category findById(Long id) {
        return this.categoryRepository.findById(id).orElse(null);
    }

    @Override
    public Boolean update(Category category) {
        try {
            this.categoryRepository.save(category);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public Boolean delete(Long id) {
        try {
            this.categoryRepository.deleteById(id);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public List<Category> getActiveCategoriesLimitedToTen() {
        return this.categoryRepository.findAll().stream()
                .filter(category -> category.getCatstatus() != null && category.getCatstatus())
                .limit(10)
                .collect(Collectors.toList());
    }

    @Override
    public List<Category> getActiveCategoriesLimitedToFour() {
        return this.categoryRepository.findAll().stream()
                .filter(category -> category.getCatstatus() != null && category.getCatstatus())
                .limit(4)
                .collect(Collectors.toList());
    }
}