package org.frogcy.furniturecustomer.product.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductSummaryDTO {
    private Integer id;
    private String name;
    private String alias;
    private boolean enabled;
    private Long price;
    private boolean inStock;
    private Integer discountPercent;
    private Long finalPrice;
    private String mainImageUrl;
    private String categoryName;
}