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
public class InventoryTransactionDTO {
    private Integer id;
    private Integer inventoryId;
    private Integer quantityChanged;
    private InventoryTransactionType type;
    private Date transactionDate;
    private String note;
}
