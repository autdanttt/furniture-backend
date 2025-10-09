package org.frogcy.furniturecustomer.order.dto;

import lombok.Getter;
import lombok.Setter;
import org.frogcy.furniturecommon.entity.order.OrderStatus;

import java.util.Date;

@Getter
@Setter
public class OrderTrackResponseDTO {
    private Integer id;
    private String notes;
    private Date updatedTime;
    private OrderStatus status;
}
