package org.frogcy.furnitureadmin.product.dto;

import org.frogcy.furniturecommon.entity.product.ProductDetail;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface ProductDetailMapper {
    ProductDetailMapper INSTANCE = Mappers.getMapper(ProductDetailMapper.class);

    ProductDetail toEntity(ProductDetailCreateDTO dto);

    ProductDetailDTO toDTO(ProductDetail entity);
}
