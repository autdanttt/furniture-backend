package org.frogcy.furnitureadmin.category;

public class CategoryAlreadyExistsException extends RuntimeException {
    public CategoryAlreadyExistsException(String s) {
        super(s);
    }
}
