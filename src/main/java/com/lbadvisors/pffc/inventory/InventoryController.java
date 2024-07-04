package com.lbadvisors.pffc.inventory;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class InventoryController {

    @Autowired
    private InventoryService inventoryService;

    @GetMapping(value = "/inventory")
    public ResponseEntity<Page<Inventory>> getAllInventory(@RequestParam int page,
            @RequestParam int size) {
        return new ResponseEntity<Page<Inventory>>(
                inventoryService.getAllInventory(page, size), HttpStatus.OK);
    }

}
