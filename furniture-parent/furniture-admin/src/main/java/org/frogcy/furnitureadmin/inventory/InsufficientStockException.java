package org.frogcy.furnitureadmin.inventory;

public class InsufficientStockException extends RuntimeException {
    public InsufficientStockException(String s) {
        super(s);
    }
}
