package org.frogcy.furniturecustomer.shippingfee;

public class CartEmptyException extends RuntimeException{
    public CartEmptyException(String s) {
        super(s);
    }
}
