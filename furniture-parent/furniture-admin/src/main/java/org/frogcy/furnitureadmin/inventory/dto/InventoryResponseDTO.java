package org.frogcy.furnitureadmin.inventory.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.frogcy.furniturecommon.entity.InventoryTransactionType;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class InventoryResponseDTO {
    private Integer id;
    private Integer productId;
    private String productName;
    private InventoryTransactionType type;
    private Integer quantity;
    private Integer quantityChanged;
    private Date transactionDate;
    private String notes;
    private Date lastUpdated;
}
