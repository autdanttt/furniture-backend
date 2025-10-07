package org.frogcy.furniturecustomer.cart;

public class CartFullException extends RuntimeException {
    public CartFullException(String s) {
        super(s);
    }
}
