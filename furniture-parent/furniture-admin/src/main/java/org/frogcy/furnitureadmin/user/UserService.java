package org.frogcy.furnitureadmin.user;

import org.frogcy.furnitureadmin.user.dto.PageResponseDTO;
import org.frogcy.furnitureadmin.user.dto.UserCreateDTO;
import org.frogcy.furnitureadmin.user.dto.UserResponseDTO;
import org.frogcy.furnitureadmin.user.dto.UserUpdateDTO;
import org.frogcy.furniturecommon.entity.User;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

public interface UserService {

    User getByEmail(String email);

    UserResponseDTO create(UserCreateDTO dto, MultipartFile multipartFile);

    UserResponseDTO update(UserUpdateDTO dto, MultipartFile multipartFile);

    UserResponseDTO findById(Integer id);

    void changeEnabled(Integer id, boolean enabled);

    void delete(Integer id, Integer userLoginId);

    PageResponseDTO<UserResponseDTO> getAllUser(int page, int size, String sortField, String sortDir, String keyword);
}
