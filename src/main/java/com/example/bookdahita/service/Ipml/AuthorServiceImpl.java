package com.example.bookdahita.service.Ipml;

import com.example.bookdahita.models.Author;
import com.example.bookdahita.repository.AuthorRepository;
import com.example.bookdahita.service.AuthorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuthorServiceImpl implements AuthorService {
    @Autowired
    private AuthorRepository authorRepository;

    @Override
    public List<Author> getAll() {
        return this.authorRepository.findAll();
    }

    @Override
    public Boolean create(Author author) {
        try {
            this.authorRepository.save(author);
            return true;
        }catch (Exception e) {
            return false;
        }
    }

    @Override
    public Author findById(Long id) {
        return this.authorRepository.findById(id).get();
    }

    @Override
    public Boolean update(Author author) {
        try {
            this.authorRepository.save(author);
            return true;
        }catch (Exception e) {
            return false;
        }
    }

    @Override
    public Boolean delete(Long id) {
        try {
            this.authorRepository.deleteById(id);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
