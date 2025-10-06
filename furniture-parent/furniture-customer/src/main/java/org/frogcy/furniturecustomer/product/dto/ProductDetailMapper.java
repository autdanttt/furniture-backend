package org.frogcy.furniturecustomer.product.dto;

import org.frogcy.furniturecommon.entity.product.ProductDetail;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface ProductDetailMapper {
    ProductDetailMapper INSTANCE = Mappers.getMapper(ProductDetailMapper.class);

    ProductDetailDTO toDTO(ProductDetail entity);
}