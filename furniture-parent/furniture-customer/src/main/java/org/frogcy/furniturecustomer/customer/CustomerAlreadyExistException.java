package org.frogcy.furniturecustomer.customer;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

public class CustomerAlreadyExistException extends RuntimeException {
    public CustomerAlreadyExistException(String s) {
        super(s);
    }
}
