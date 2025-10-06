package org.frogcy.furniturecustomer.product.dto;

import org.frogcy.furniturecommon.entity.product.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    ProductMapper INSTANCE = Mappers.getMapper(ProductMapper.class);

    ProductResponseDTO toDto(Product product);

    ProductSummaryDTO toSummaryDTO(Product product);
}
