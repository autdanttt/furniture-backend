package org.frogcy.furniturecustomer.inventory.impl;

import org.frogcy.furniturecommon.entity.product.Product;
import org.frogcy.furniturecustomer.inventory.InventoryRepository;
import org.frogcy.furniturecustomer.inventory.InventoryService;
import org.springframework.stereotype.Service;

@Service
public class InventoryServiceImpl implements InventoryService {
    private final InventoryRepository inventoryRepository;
    public InventoryServiceImpl(InventoryRepository inventoryRepository) {
        this.inventoryRepository = inventoryRepository;
    }
    @Override
    public boolean inStock(Product product) {
        return inventoryRepository.findByProduct(product)
                .map(inv -> inv.getQuantity() > 0)
                .orElse(false);
    }
}
