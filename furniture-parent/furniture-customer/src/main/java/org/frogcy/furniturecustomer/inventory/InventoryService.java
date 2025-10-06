package org.frogcy.furniturecustomer.inventory;

import org.frogcy.furniturecommon.entity.product.Product;

public interface InventoryService {
    boolean inStock(Product product);
}
