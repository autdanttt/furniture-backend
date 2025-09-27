package org.frogcy.furnitureadmin.user.dto;

import org.frogcy.furniturecommon.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    User toEntity(UserCreateDTO dto);

    UserResponseDTO toDto(User entity);
}
