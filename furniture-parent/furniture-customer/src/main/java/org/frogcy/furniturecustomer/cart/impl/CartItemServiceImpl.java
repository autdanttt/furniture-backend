package org.frogcy.furniturecustomer.cart.impl;

import org.frogcy.furniturecommon.entity.CartItem;
import org.frogcy.furniturecommon.entity.Customer;
import org.frogcy.furniturecommon.entity.product.Product;
import org.frogcy.furniturecustomer.cart.CartItemNotFoundException;
import org.frogcy.furniturecustomer.cart.CartItemRepository;
import org.frogcy.furniturecustomer.cart.CartItemService;
import org.frogcy.furniturecustomer.cart.dto.CartItemRequestDTO;
import org.frogcy.furniturecustomer.cart.dto.CartItemResponseDTO;
import org.frogcy.furniturecustomer.cart.dto.CartSummaryDTO;
import org.frogcy.furniturecustomer.product.ProductNotFoundException;
import org.frogcy.furniturecustomer.product.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CartItemServiceImpl implements CartItemService {
    private static final int MAX_CART_ITEMS = 20;
    private CartItemRepository cartItemRepository;
    private ProductRepository productRepository;

    public CartItemServiceImpl(CartItemRepository cartItemRepository, ProductRepository productRepository) {
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
    }


    @Override
    public void addToCart(Customer customer, CartItemRequestDTO request) {
        List<CartItem> currentItems = cartItemRepository.findByCustomerId(customer.getId());

        if (currentItems.size() >= MAX_CART_ITEMS) {
            throw new IllegalStateException("Giỏ hàng chỉ được chứa tối đa " + MAX_CART_ITEMS + " sản phẩm.");
        }

        Product product = productRepository.findById(request.getProductId()).orElseThrow(
                ()-> new ProductNotFoundException("Product " + request.getProductId() + " not found.")
        );

        // Nếu sản phẩm đã tồn tại -> update quantity
        Optional<CartItem> existingItem = currentItems.stream()
                .filter(item -> item.getProduct().getId().equals(request.getProductId()))
                .findFirst();

        if (existingItem.isPresent()) {
            CartItem item = existingItem.get();
            item.setQuantity(item.getQuantity() + request.getQuantity());
            cartItemRepository.save(item);
        }

        // Thêm mới
        CartItem newItem = new CartItem();
        newItem.setCustomer(customer);
        newItem.setQuantity(request.getQuantity());
        newItem.setProduct(product);
        cartItemRepository.save(newItem);
    }

    @Override
    public CartSummaryDTO getCartItems(Customer customer) {

        List<CartItem> currentItems = cartItemRepository.findByCustomerId(customer.getId());

        List<CartItemResponseDTO> items = currentItems.stream().map(
                cartItem -> {
                    CartItemResponseDTO item = new CartItemResponseDTO();
                    item.setProductId(cartItem.getProduct().getId());
                    item.setQuantity(cartItem.getQuantity());
                    item.setProductName(cartItem.getProduct().getName());
                    item.setProductImageUrl(cartItem.getProduct().getMainImage().getImageUrl());
                    item.setPrice(cartItem.getProduct().getFinalPrice());
                    item.setSubTotalItem(cartItem.getQuantity() * cartItem.getProduct().getFinalPrice());
                    return item;
                }).toList();
        int totalItems = items.stream().mapToInt(CartItemResponseDTO::getQuantity).sum();
        Long subTotal = items.stream().mapToLong(CartItemResponseDTO::getSubTotalItem).sum();

        CartSummaryDTO summary = new CartSummaryDTO();
        summary.setItems(items);
        summary.setTotalItems(totalItems);
        summary.setSubTotal(subTotal);

        return summary;
    }

    @Override
    public void updateQuantity(Customer customer, CartItemRequestDTO request) {
        CartItem item = cartItemRepository.findByCustomerIdAndProductId(customer.getId(),request.getProductId())
                .orElseThrow(() -> new CartItemNotFoundException("Sản phẩm không có trong giỏ hàng"));

        if (request.getQuantity() <= 0) {
            // Nếu số lượng <= 0 thì xóa khỏi giỏ luôn
            cartItemRepository.delete(item);
            return;
        }

        item.setQuantity(request.getQuantity());
        cartItemRepository.save(item);
    }

    @Override
    public void deleteItem(Customer customer, Integer productId) {
        CartItem item = cartItemRepository.findByCustomerIdAndProductId(customer.getId(),productId)
                .orElseThrow(() -> new CartItemNotFoundException("Sản phẩm không có trong giỏ hàng"));

        cartItemRepository.delete(item);
    }

}
