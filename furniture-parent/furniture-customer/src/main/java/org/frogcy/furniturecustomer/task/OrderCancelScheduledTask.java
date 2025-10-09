package org.frogcy.furniturecustomer.task;

import jakarta.transaction.Transactional;
import org.frogcy.furniturecommon.entity.Inventory;
import org.frogcy.furniturecommon.entity.order.*;
import org.frogcy.furniturecustomer.inventory.InventoryRepository;
import org.frogcy.furniturecustomer.order.OrderRepository;
import org.frogcy.furniturecustomer.order.OrderTrackRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;

@Component
@EnableScheduling
public class OrderCancelScheduledTask {
    private static final Logger logger = LoggerFactory.getLogger(OrderCancelScheduledTask.class);

    private final OrderRepository orderRepository;
    private final OrderTrackRepository orderTrackRepository;
    private final InventoryRepository inventoryRepository;

    public OrderCancelScheduledTask(OrderRepository orderRepository, OrderTrackRepository orderTrackRepository, InventoryRepository inventoryRepository) {
        this.orderRepository = orderRepository;
        this.orderTrackRepository = orderTrackRepository;
        this.inventoryRepository = inventoryRepository;
    }

    @Transactional
    @Scheduled(fixedRate = 3600000) // chạy mỗi 1 tiếng
    public void autoCancelUnpaidOrders() {
        Date twoDaysAgo = Date.from(
                Instant.now().minus(2, ChronoUnit.DAYS)
//                Instant.now().minus(1, ChronoUnit.HOURS)
        );

        List<Order> unpaidOrders = orderRepository.findByPaymentStatusAndStatusAndOrderTimeBefore(
                PaymentStatus.PENDING, OrderStatus.NEW, twoDaysAgo
        );

        for (Order order : unpaidOrders) {
            if(order.getPaymentMethod().equals(PaymentMethod.COD)){
                continue;
            }
            order.setStatus(OrderStatus.CANCELLED);
            order.setPaymentStatus(PaymentStatus.FAILED);

            OrderTrack track = new OrderTrack();
            track.setOrder(order);
            track.setStatus(OrderStatus.CANCELLED);
            track.setNotes("Order automatically cancelled after 2 days of no payment");
            track.setUpdatedTime(new Date());

            orderTrackRepository.save(track);
            orderRepository.save(order);

            for (OrderDetail detail : order.getOrderDetails()) {
                Inventory inv = inventoryRepository.findByProduct(detail.getProduct())
                        .orElseThrow(() -> new IllegalStateException("No inventory for product " + detail.getProduct().getName()));
                inv.setQuantity(inv.getQuantity() + detail.getQuantity());
                inventoryRepository.save(inv);
            }

            logger.info("Auto-cancelled order #{}", order.getId());
        }


    }



}
