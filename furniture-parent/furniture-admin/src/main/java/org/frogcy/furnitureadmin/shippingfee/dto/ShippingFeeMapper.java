package org.frogcy.furnitureadmin.shippingfee.dto;

import org.frogcy.furniturecommon.entity.ShippingFee;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface ShippingFeeMapper {
    ShippingFeeMapper INSTANCE = Mappers.getMapper(ShippingFeeMapper.class);

    ShippingFee toEntity(ShippingFeeRequestDTO dto);
    ShippingFeeResponseDTO toDto(ShippingFee entity);

    void updateEntityFromDto(ShippingFeeUpdateDTO dto, @MappingTarget ShippingFee entity);
}
