package org.frogcy.furnitureadmin.security.jwt;

public class JwtValidationException extends Exception {
    public JwtValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}
