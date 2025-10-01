package org.frogcy.furnitureadmin.user.impl;

public class UserAlreadyExistsException extends RuntimeException {

    public UserAlreadyExistsException(String s) {
        super(s);
    }
}
