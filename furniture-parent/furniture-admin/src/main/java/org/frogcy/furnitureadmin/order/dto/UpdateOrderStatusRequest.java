package org.frogcy.furnitureadmin.order.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.frogcy.furniturecommon.entity.order.OrderStatus;

@Getter
@Setter
public class UpdateOrderStatusRequest {
    @NotNull
    private OrderStatus status;
    private String notes;
}
