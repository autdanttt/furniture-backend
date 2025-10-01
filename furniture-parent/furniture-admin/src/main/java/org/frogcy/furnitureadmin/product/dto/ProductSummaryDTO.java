package org.frogcy.furnitureadmin.product.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.frogcy.furnitureadmin.category.dto.CategoryResponseDTO;
import org.frogcy.furniturecommon.entity.product.ProductImage;

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
    private Integer discountPercent;
    private Long finalPrice;
    private ProductImage mainImage;
    private CategoryResponseDTO category;
}
