package org.frogcy.furnitureadmin.category.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryResponseDTO {
    private Integer id;
    private String name;
    private String alias;
    private String image;
    private boolean enabled;
}
