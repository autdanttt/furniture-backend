package org.frogcy.furnitureadmin.product.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductUpdateDTO {
    @NotNull
    private Integer id;
    @NotNull(message = "Name can not be null")
    @Length(min = 1, max = 256, message = "Name must be between 1-256 characters")
    private String name;
    @NotNull(message = "Alias can not be null")
    @Length(min = 1, max = 256, message = "Alias must be between 1-256 characters")
    private String alias;
    @NotNull
    private Integer categoryId;
    @NotNull(message = "Short Description can not be null")
    @Length(min = 3, max = 512, message = "Short description must be between 3-512 characters")
    private String shortDescription;
    @NotNull(message = "Full Description can not be null")
    @Length(min = 3, max = 512, message = "Full description must be between 3-512 characters")
    private String fullDescription;
    @NotNull
    private boolean enabled;
    @NotNull
    private boolean inStock;
    @NotNull
    @Range(min = 0, message = "Cost must be greater than 0")
    private Long cost;
    @NotNull
    @Range(min = 0, message = "Price must be greater than 0")
    private Long price;
    @Range(min = 0, max = 100, message = "Discount percent must be between 0 and 100")
    private Integer discountPercent;
    @NotNull
    @Range(min = 0, message = "Length must be greater than 0")
    private float length;
    @NotNull
    private float width;
    @NotNull
    private float height;
    @NotNull
    private float weight;
    private List<Integer> newImagesOrder;
    private List<ImageOrder> retainedImages;
    private List<ProductDetailCreateDTO> newProductDetails;
    private List<Integer> retainedProductDetailIds;
}
