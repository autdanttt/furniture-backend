package org.frogcy.furnitureadmin.product.dto;


import org.frogcy.furniturecommon.entity.product.Product;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    ProductMapper INSTANCE = Mappers.getMapper(ProductMapper.class);

    Product toEntity(ProductCreateDTO dto);

    ProductResponseDTO toDto(Product product);

}
