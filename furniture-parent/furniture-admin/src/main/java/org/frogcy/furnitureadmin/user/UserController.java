package org.frogcy.furnitureadmin.user;

import jakarta.annotation.Nullable;
import jakarta.validation.Valid;
import org.frogcy.furnitureadmin.security.CustomUserDetails;
import org.frogcy.furnitureadmin.user.dto.PageResponseDTO;
import org.frogcy.furnitureadmin.user.dto.UserCreateDTO;
import org.frogcy.furnitureadmin.user.dto.UserResponseDTO;
import org.frogcy.furnitureadmin.user.dto.UserUpdateDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


@RestController
@RequestMapping("/api/user")
@Validated
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createUser(@RequestPart(name = "user") UserCreateDTO userCreateDTO,
                                        @RequestPart(name = "image") @Nullable MultipartFile multipartFile) {
        UserResponseDTO dto = userService.create(userCreateDTO, multipartFile);

        return new ResponseEntity<>(dto, HttpStatus.CREATED);
    }


    @PutMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateUser(@RequestPart(name = "user") @Valid UserUpdateDTO userUpdateDTO,
                                        @RequestPart(name = "image") @Nullable MultipartFile multipartFile) {
        UserResponseDTO dto = userService.update(userUpdateDTO, multipartFile);

        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable("id") Integer id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Integer userLoginId = null;
        if (auth != null && auth.isAuthenticated() && auth.getPrincipal() instanceof CustomUserDetails user) {
            userLoginId = user.getUser().getId();
        }

        userService.delete(id, userLoginId);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUser(@PathVariable("id") Integer id) {
        UserResponseDTO dto = userService.findById(id);
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> changeEnabled(@PathVariable("id") Integer id, @RequestParam boolean enabled) {
        userService.changeEnabled(id, enabled);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<?> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortField,
            @RequestParam(defaultValue = "asc") String sortDir,
            @RequestParam(defaultValue = "", required = false) String keyword
    ) {
        PageResponseDTO<UserResponseDTO> response = userService.getAllUser(page, size, sortField, sortDir, keyword);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
