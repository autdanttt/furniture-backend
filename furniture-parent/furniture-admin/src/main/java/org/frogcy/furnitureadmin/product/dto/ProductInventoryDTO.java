package org.frogcy.furnitureadmin.product.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductInventoryDTO {
    private Integer productId;
    private String productName;
    private String imageUrl;
    private String categoryName;
    private Integer quantity;
    private Date lastUpdated;
}
