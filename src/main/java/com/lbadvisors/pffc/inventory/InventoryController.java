package com.lbadvisors.pffc.inventory;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class InventoryController {

    @Autowired
    private InventoryService inventoryService;

    @GetMapping(value = "/inventory")
    public ResponseEntity<List<Inventory>> getAllInventory() {
        return new ResponseEntity<>(
                inventoryService.getAllInventory(), HttpStatus.OK);
    }

}
