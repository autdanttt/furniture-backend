package org.frogcy.furniturecustomer.cart;

import jakarta.validation.Valid;
import org.frogcy.furniturecommon.entity.Customer;
import org.frogcy.furniturecustomer.cart.dto.CartItemRequestDTO;
import org.frogcy.furniturecustomer.cart.dto.CartSummaryDTO;
import org.frogcy.furniturecustomer.customer.CustomerRepository;
import org.frogcy.furniturecustomer.customer.dto.CustomerNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/cart")
@Validated
public class CartItemController {
    private final CartItemService cartItemService;
    private final CustomerRepository customerRepository;

    public CartItemController(CartItemService cartItemService, CustomerRepository customerRepository) {
        this.cartItemService = cartItemService;
        this.customerRepository = customerRepository;
    }

    @PostMapping
    public ResponseEntity<?> createCartItem(@RequestBody @Valid CartItemRequestDTO request){
        Customer customer = getCustomer();

        cartItemService.addToCart(customer, request);
        Map<String, String> map = new HashMap<>();
        map.put("message", "Successfully created cart item");

        return new ResponseEntity<>(map, HttpStatus.OK);
    }

    @PutMapping
    public ResponseEntity<?> updateCartItem(@RequestBody @Valid CartItemRequestDTO request){
        Customer customer = getCustomer();
        cartItemService.updateQuantity(customer, request);

        Map<String, String> map = new HashMap<>();
        map.put("message", "Successfully updated cart item");

        return new ResponseEntity<>(map, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<?> getCartItems() {
        Customer customer = getCustomer();

        CartSummaryDTO response = cartItemService.getCartItems(customer);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<?> deleteCartItem(@PathVariable Integer productId) {
        Customer customer = getCustomer();
        cartItemService.deleteItem(customer, productId);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }




    private Customer getCustomer() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return customerRepository.findByEmail(email)
                .orElseThrow(() -> new CustomerNotFoundException("User not found: " + email));
    }
}
