package org.frogcy.furnitureadmin.category.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CategoryCreateDTO {
    @NotNull(message = "Name can not be null")
    @Length(min = 3, max = 64, message = "Name must be between 3-64 characters")
    private String name;
    @NotNull(message = "Alias can not be null")
    @Length(min = 3, max = 64, message = "Alias must be between 3-64 characters")
    private String alias;
    @NotNull
    private boolean enabled;
    private Integer parentId;
}
