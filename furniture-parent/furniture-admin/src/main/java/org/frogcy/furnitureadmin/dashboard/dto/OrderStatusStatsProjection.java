package org.frogcy.furnitureadmin.dashboard.dto;

import org.frogcy.furniturecommon.entity.order.OrderStatus;

public interface OrderStatusStatsProjection {
    OrderStatus getStatus();
    Long getOrderCount();
}