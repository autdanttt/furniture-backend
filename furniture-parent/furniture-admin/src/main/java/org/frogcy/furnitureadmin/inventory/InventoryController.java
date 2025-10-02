package org.frogcy.furnitureadmin.inventory;

import jakarta.validation.Valid;
import org.frogcy.furnitureadmin.inventory.dto.InventoryResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/inventory")
@Validated
public class InventoryController {
    private InventoryService inventoryService;

    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @PostMapping("/{productId}/import")
    public ResponseEntity<?> importProduct(@PathVariable("productId") Integer productId, @RequestBody @Valid InventoryRequestDTO dto) {
        InventoryResponseDTO response = inventoryService.importProduct(productId, dto);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/{productId}/export")
    public ResponseEntity<?> exportProduct(@PathVariable("productId") Integer productId, @RequestBody @Valid InventoryRequestDTO dto) {
        InventoryResponseDTO response = inventoryService.exportProduct(productId, dto);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("/{productId}")
    public ResponseEntity<?> updateQuantity(@PathVariable("productId") Integer productId,@RequestBody @Valid InventoryRequestDTO dto){
        InventoryResponseDTO response = inventoryService.update(productId, dto);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/{productId}")
    public ResponseEntity<?> getInventory(@PathVariable("productId") Integer productId) {
        InventoryResponseDTO response = inventoryService.get(productId);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}