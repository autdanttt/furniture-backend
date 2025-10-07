package org.frogcy.furniturecustomer.cart.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CartItemResponseDTO {
    private Integer productId;
    private String productName;
    private String productImageUrl;
    private Long price;
    private Integer quantity;
    private Long subTotalItem;
}
