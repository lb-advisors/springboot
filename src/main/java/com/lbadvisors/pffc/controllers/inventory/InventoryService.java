package com.lbadvisors.pffc.controllers.inventory;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Pageable;

@Service
public class InventoryService {

        @Autowired
        InventoryRepository inventoryRepository;

        @Autowired
        ModelMapper modelMapper;

        public Page<Inventory> getAllInventory(int page, int size, String search) {
                Pageable pageable = PageRequest.of(page, size);
                return inventoryRepository.searchInventoryItems(search, pageable);
        }

}
