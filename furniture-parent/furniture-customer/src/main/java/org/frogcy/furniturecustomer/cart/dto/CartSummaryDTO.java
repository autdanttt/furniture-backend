package org.frogcy.furniturecustomer.cart.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CartSummaryDTO {
    private List<CartItemResponseDTO> items;
    private int totalItems;
    private Long subTotal;
}
