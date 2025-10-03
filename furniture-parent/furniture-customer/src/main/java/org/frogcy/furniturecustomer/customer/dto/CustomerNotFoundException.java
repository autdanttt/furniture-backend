package org.frogcy.furniturecustomer.customer.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

public class CustomerNotFoundException extends RuntimeException {
    public CustomerNotFoundException(String s) {
        super(s);
    }
}
