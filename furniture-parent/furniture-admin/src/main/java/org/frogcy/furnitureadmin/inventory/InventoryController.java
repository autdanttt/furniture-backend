package org.frogcy.furnitureadmin.inventory;

import jakarta.validation.Valid;
import org.frogcy.furnitureadmin.inventory.dto.InventoryResponseDTO;
import org.frogcy.furnitureadmin.inventory.dto.InventoryTransactionDTO;
import org.frogcy.furnitureadmin.product.dto.ProductInventoryDTO;
import org.frogcy.furnitureadmin.user.dto.PageResponseDTO;
import org.frogcy.furniturecommon.entity.InventoryTransaction;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventory")
@Validated
public class InventoryController {
    private InventoryService inventoryService;

    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @GetMapping
    public ResponseEntity<?> getListInventory(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortField,
            @RequestParam(defaultValue = "asc") String sortDir,
            @RequestParam(defaultValue = "", required = false) String keyword
    ) {
        PageResponseDTO<ProductInventoryDTO> response = inventoryService.getAllInventory(page, size, sortField, sortDir, keyword);
        return ResponseEntity.ok(response);
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

    @GetMapping("/transaction/{inventoryId}")
    public ResponseEntity<?> getInventoryTransaction(@PathVariable("inventoryId") Integer inventoryId) {
        List<InventoryTransactionDTO> list = inventoryService.getListInventoryTransaction(inventoryId);


        return new ResponseEntity<>(list, HttpStatus.OK);
    }

}