package org.frogcy.furnitureadmin.user;

import org.frogcy.furnitureadmin.user.dto.UserCreateDTO;
import org.frogcy.furnitureadmin.user.dto.UserResponseDTO;
import org.frogcy.furniturecommon.entity.User;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

public interface UserService {

    User getByEmail(String email);

    UserResponseDTO create(UserCreateDTO dto, MultipartFile multipartFile);
}
