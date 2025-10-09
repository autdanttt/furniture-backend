package org.frogcy.furniturecustomer.order;

import org.frogcy.furniturecommon.entity.Customer;
import org.frogcy.furniturecommon.entity.order.OrderTrack;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderTrackRepository extends JpaRepository<OrderTrack, Integer> {
    List<OrderTrack> findByOrderIdOrderByUpdatedTimeDesc(Integer orderId);
}
