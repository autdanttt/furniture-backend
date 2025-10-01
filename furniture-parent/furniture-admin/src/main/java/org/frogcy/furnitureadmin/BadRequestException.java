package org.frogcy.furnitureadmin;

public class BadRequestException extends RuntimeException {
    public BadRequestException(String message) {
        super(message);
    }

}