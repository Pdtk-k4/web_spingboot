package com.example.bookdahita.service.Ipml;

import com.example.bookdahita.models.Inventory;
import com.example.bookdahita.repository.InventoryRepository;
import com.example.bookdahita.service.InventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class InventoryServiceImpl implements InventoryService {
    @Autowired
    private InventoryRepository inventoryRepository;

    @Override
    public Boolean update(Inventory inventories) {
        try {
            this.inventoryRepository.save(inventories);
            return true;
        }catch (Exception e) {
            return false;
        }
    }
}
