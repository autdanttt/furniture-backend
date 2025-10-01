package org.frogcy.furnitureadmin.inventory;

import jakarta.validation.Valid;
import org.frogcy.furnitureadmin.inventory.dto.InventoryResponseDTO;
import org.frogcy.furniturecommon.entity.product.Product;

public interface InventoryService {
    boolean inStock(Product product);
    InventoryResponseDTO importProduct(Integer productId, InventoryRequestDTO dto);

    InventoryResponseDTO exportProduct(Integer productId, InventoryRequestDTO dto);

    InventoryResponseDTO update(Integer productId, @Valid InventoryUpdateDTO dto);

    InventoryResponseDTO get(Integer productId);
}
