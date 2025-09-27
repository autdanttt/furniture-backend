package org.frogcy.furnitureadmin.user;

import jakarta.annotation.Nullable;
import org.frogcy.furnitureadmin.user.dto.UserCreateDTO;
import org.frogcy.furnitureadmin.user.dto.UserResponseDTO;
import org.frogcy.furniturecommon.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createUser(@RequestPart(name = "user") UserCreateDTO userCreateDTO,
                                        @RequestPart(name = "image") @Nullable MultipartFile multipartFile) {
        UserResponseDTO dto = userService.create(userCreateDTO, multipartFile);

        return new ResponseEntity<>(dto, HttpStatus.CREATED);
    }
}
