package org.frogcy.furniturecustomer.cart;

public class ProductAlreadyInCartException extends RuntimeException {
    public ProductAlreadyInCartException(String s) {
        super(s);
    }
}
