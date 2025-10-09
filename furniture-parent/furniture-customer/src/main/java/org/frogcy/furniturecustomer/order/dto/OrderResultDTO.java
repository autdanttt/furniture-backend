package org.frogcy.furniturecustomer.order.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.frogcy.furniturecommon.entity.order.OrderStatus;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderResultDTO {
    private Integer orderId;
    private OrderStatus orderStatus;
    private long total;
    private Date orderTime;
}
