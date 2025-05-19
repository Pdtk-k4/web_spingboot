package com.example.bookdahita.service;

import com.example.bookdahita.models.Supplier;

import java.util.List;

public interface SupplierService {
    List<Supplier> getAll();
    Boolean create(Supplier supplier);
    Supplier findById(Long id);
    Boolean update(Supplier supplier);
    Boolean delete(Long id);
}
