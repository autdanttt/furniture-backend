package org.frogcy.furniturecustomer.order;

import org.frogcy.furniturecommon.entity.Customer;
import org.frogcy.furniturecommon.entity.order.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {
    List<Order> findAllByCustomerOrderByOrderTimeDesc(Customer customer);
}
