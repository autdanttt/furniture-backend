package org.frogcy.furniturecustomer.cart;

import jakarta.validation.Valid;
import org.frogcy.furniturecommon.entity.Customer;
import org.frogcy.furniturecustomer.cart.dto.CartItemRequestDTO;
import org.frogcy.furniturecustomer.cart.dto.CartSummaryDTO;

public interface CartItemService {
    void addToCart(Customer customer, @Valid CartItemRequestDTO request);

    CartSummaryDTO getCartItems(Customer customer);

    void updateQuantity(Customer customer, CartItemRequestDTO request);

    void deleteItem(Customer customer, Integer productId);
}
