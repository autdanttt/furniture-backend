package org.frogcy.furnitureadmin.inventory;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Range;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class InventoryUpdateDTO {
    @NotNull
    @Range(min = 0, message = "Quantity must be greater than 0.")
    private Integer quantity;
}
