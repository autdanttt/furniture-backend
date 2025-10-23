package org.frogcy.furnitureadmin.inventory;

import jakarta.validation.Valid;
import org.frogcy.furnitureadmin.inventory.dto.InventoryResponseDTO;
import org.frogcy.furnitureadmin.inventory.dto.InventoryTransactionDTO;
import org.frogcy.furnitureadmin.product.dto.ProductInventoryDTO;
import org.frogcy.furnitureadmin.user.dto.PageResponseDTO;
import org.frogcy.furniturecommon.entity.InventoryTransaction;
import org.frogcy.furniturecommon.entity.product.Product;

import java.util.List;

public interface InventoryService {
    boolean inStock(Product product);
    InventoryResponseDTO importProduct(Integer productId, InventoryRequestDTO dto);

    InventoryResponseDTO exportProduct(Integer productId, InventoryRequestDTO dto);

    InventoryResponseDTO update(Integer productId, @Valid InventoryRequestDTO dto);

    InventoryResponseDTO get(Integer productId);

    PageResponseDTO<ProductInventoryDTO> getAllInventory(int page, int size, String sortField, String sortDir, String keyword);

    List<InventoryTransactionDTO> getListInventoryTransaction(Integer inventoryId);
}
