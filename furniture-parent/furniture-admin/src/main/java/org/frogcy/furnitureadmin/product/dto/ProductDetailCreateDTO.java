package org.frogcy.furnitureadmin.product.dto;

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
public class ProductDetailCreateDTO {
    @NotNull
    @Length(min = 1, max = 256, message = "Name must be between 1-256")
    private String name;
    @NotNull
    @Length(min = 1, max = 256, message = "Value must be between 1-256")
    private String value;
}
