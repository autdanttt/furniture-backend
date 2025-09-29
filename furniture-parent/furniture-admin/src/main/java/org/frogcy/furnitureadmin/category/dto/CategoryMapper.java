package org.frogcy.furnitureadmin.category.dto;

import org.frogcy.furniturecommon.entity.Category;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    CategoryMapper INSTANCE = Mappers.getMapper(CategoryMapper.class);

    Category toEntity(CategoryCreateDTO dto);

//    Category toEntity(CategoryCreateDTO dto);
    CategoryResponseDTO toDto(Category category);
}
