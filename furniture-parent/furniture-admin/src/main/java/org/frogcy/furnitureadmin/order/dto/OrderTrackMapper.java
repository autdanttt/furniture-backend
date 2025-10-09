package org.frogcy.furnitureadmin.order.dto;

import org.frogcy.furniturecommon.entity.order.OrderTrack;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface OrderTrackMapper {
    OrderTrackDTO toDto(OrderTrack orderTrack);
}
