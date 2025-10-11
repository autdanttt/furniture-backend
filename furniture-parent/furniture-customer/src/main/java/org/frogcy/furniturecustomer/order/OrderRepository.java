package org.frogcy.furniturecustomer.order;

import org.frogcy.furniturecommon.entity.Customer;
import org.frogcy.furniturecommon.entity.order.Order;
import org.frogcy.furniturecommon.entity.order.OrderStatus;
import org.frogcy.furniturecommon.entity.order.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {
    List<Order> findAllByCustomerOrderByOrderTimeDesc(Customer customer);

    List<Order> findByPaymentStatusAndStatusAndOrderTimeBefore(PaymentStatus paymentStatus, OrderStatus orderStatus, Date twoDaysAgo);

    Optional<Order> findByPaymentIntentId(String paymentIntentId);
}
