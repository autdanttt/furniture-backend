package org.frogcy.furnitureadmin.inventory;

public class InventoryNotFoundException extends RuntimeException {
    public InventoryNotFoundException(String noInventoryFoundForProduct) {
        super(noInventoryFoundForProduct);
    }
}
