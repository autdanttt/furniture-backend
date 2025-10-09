package org.frogcy.furnitureadmin.customer;

public class CustomerNotFoundException extends RuntimeException {
    public CustomerNotFoundException(String s) {
        super(s);
    }
}
