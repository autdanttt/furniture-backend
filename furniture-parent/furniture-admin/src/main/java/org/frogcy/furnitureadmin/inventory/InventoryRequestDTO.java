package org.frogcy.furnitureadmin.inventory;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class InventoryRequestDTO {
    private Integer quantity;
    private String note;
}
