package org.frogcy.furniturecustomer.cart;

import org.frogcy.furniturecommon.entity.CartItem;
import org.frogcy.furniturecommon.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Integer> {
    List<CartItem> findByCustomerId(Integer customerId);

    Optional<CartItem> findByCustomerIdAndProductId(Integer customerId, Integer productId);
}
