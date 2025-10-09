package org.frogcy.furniturecustomer.order.dto;

import org.frogcy.furniturecommon.entity.order.OrderTrack;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface OrderTrackMapper {

    OrderTrackResponseDTO toDto(OrderTrack orderTrack);
}
