package org.frogcy.furnitureadmin.product;

public class ProductAlreadyExistsException extends RuntimeException {
    public ProductAlreadyExistsException(String s) {
        super(s);
    }
}
