package com.lbadvisors.pffc.inventory;

import java.util.List;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class InventoryService {

        @Autowired
        InventoryRepository inventoryRepository;

        @Autowired
        ModelMapper modelMapper;

        public List<Inventory> getAllInventory() {
                return this.inventoryRepository.findAll();
        }

}
