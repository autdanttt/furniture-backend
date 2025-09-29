package org.frogcy.furnitureadmin.category.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CategoryUpdateDTO {
    @NotNull(message = "Id can not be null")
    private Integer id;
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
