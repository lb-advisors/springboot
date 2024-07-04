package com.lbadvisors.pffc.inventory;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
public class InventoryService {

        @Autowired
        InventoryRepository inventoryRepository;

        @Autowired
        ModelMapper modelMapper;

        public Page<Inventory> getAllInventory(int page, int size) {
                return this.inventoryRepository.findAll(PageRequest.of(page, size));
        }

}
