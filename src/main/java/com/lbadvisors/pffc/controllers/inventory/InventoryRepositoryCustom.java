package com.lbadvisors.pffc.controllers.inventory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface InventoryRepositoryCustom {
    Page<Inventory> searchInventoryItems(String search, Pageable pageable);
}