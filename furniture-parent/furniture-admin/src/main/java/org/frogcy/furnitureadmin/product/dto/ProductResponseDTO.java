package org.frogcy.furnitureadmin.product.dto;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.frogcy.furnitureadmin.category.dto.CategoryResponseDTO;
import org.frogcy.furniturecommon.entity.product.ProductDetail;
import org.frogcy.furniturecommon.entity.product.ProductImage;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponseDTO {
    private Integer id;
    private String name;
    private String alias;
    private CategoryResponseDTO category;
    private String shortDescription;
    private String fullDescription;
    private boolean enabled;
    private boolean inStock;
    private Long cost;
    private Long price;
    private Integer discountPercent;
    private Long finalPrice;
    private float length;
    private float width;
    private float height;
    private float weight;
    private ProductImage mainImage;
    private List<ProductImageDTO> images;
    private List<ProductDetailDTO> details;

}
