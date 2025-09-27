package org.frogcy.furnitureadmin;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Setter
@Getter
public class ErrorDTO {
    private Date timestamp;
    private int status;
    private String path;
    private List<String> errors = new ArrayList<>();

    public void addError(String error) {
        this.errors.add(error);
    }
}
