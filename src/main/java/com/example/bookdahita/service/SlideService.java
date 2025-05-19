package com.example.bookdahita.service;

import com.example.bookdahita.models.Product;
import com.example.bookdahita.models.Slide;

import java.util.List;

public interface SlideService {
    List<Slide> getAll();
    Boolean create(Slide slide);
    Slide findById(Long id);
    Boolean update(Slide slide);
    Boolean delete(Long id);
    List<Slide> getActiveSlidesLimitedToFour();
}
