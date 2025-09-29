package org.frogcy.furnitureadmin.category.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CategoryResponseDTO {
    private Integer id;
    private String name;
    private String alias;
    private boolean enabled;
    private List<CategoryResponseDTO> children;
}
