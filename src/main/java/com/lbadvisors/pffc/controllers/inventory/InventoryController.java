package com.lbadvisors.pffc.controllers.inventory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;

@RestController
public class InventoryController {

    @Autowired
    private InventoryService inventoryService;

    @GetMapping(value = "/inventory")
    @Operation(summary = "Get entire inventory")
    public ResponseEntity<Page<Inventory>> getAllInventory(@RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size, @RequestParam(required = false) String search) {
        return new ResponseEntity<Page<Inventory>>(
                inventoryService.getAllInventory(page, size, search), HttpStatus.OK);
    }

}
