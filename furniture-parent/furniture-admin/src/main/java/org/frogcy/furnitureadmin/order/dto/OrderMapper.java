package org.frogcy.furnitureadmin.order.dto;

import org.frogcy.furniturecommon.entity.order.Order;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface OrderMapper {
    OrderSummaryDTO toSummaryDTO(Order order);

}
