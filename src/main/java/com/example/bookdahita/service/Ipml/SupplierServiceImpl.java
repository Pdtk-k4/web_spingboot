package com.example.bookdahita.service.Ipml;

import com.example.bookdahita.models.Supplier;
import com.example.bookdahita.repository.SupplierRepository;
import com.example.bookdahita.service.SupplierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SupplierServiceImpl implements SupplierService {

    @Autowired
    private SupplierRepository supplierRepository;

    @Override
    public List<Supplier> getAll() {
        return this.supplierRepository.findAll();
    }

    @Override
    public Boolean create(Supplier supplier) {
        try {
            this.supplierRepository.save(supplier);
            return true;
        }catch (Exception e){
            return false;
        }
    }

    @Override
    public Supplier findById(Long id) {
        return this.supplierRepository.findById(id).get();
    }

    @Override
    public Boolean update(Supplier supplier) {
        try {
            this.supplierRepository.save(supplier);
            return true;
        }catch (Exception e){
            return false;
        }
    }

    @Override
    public Boolean delete(Long id) {
        try {
            this.supplierRepository.deleteById(id);
            return true;
        }catch (Exception e){
            return false;
        }
    }
}
