package org.frogcy.furnitureadmin.user.impl;

import jakarta.transaction.Transactional;
import org.frogcy.furnitureadmin.media.AssetService;
import org.frogcy.furnitureadmin.user.*;
import org.frogcy.furnitureadmin.user.dto.*;
import org.frogcy.furniturecommon.entity.Role;
import org.frogcy.furniturecommon.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserServiceImpl implements UserService {
    private final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);
    private final RoleRepository roleRepository;
    private final AssetService assetService;

    private PasswordEncoder passwordEncoder;
    private UserMapper userMapper;
    private UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository, UserMapper userMapper, PasswordEncoder passwordEncoder, RoleRepository roleRepository, AssetService assetService) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
        this.assetService = assetService;
    }

    @Override
    public User getByEmail(String email){
        return userRepository.getUserByEmail(email);
    }

    @Override
    public UserResponseDTO create(UserCreateDTO dto, MultipartFile multipartFile) {
        if(userRepository.findByEmail(dto.getEmail()).isPresent()){
            throw new UserAlreadyExistsException("User already exists with email: " + dto.getEmail());

        }

        String avatar = "";
        if(multipartFile != null){
            avatar = assetService.uploadToCloudinary(multipartFile, "avatar");
        }

        User user = userMapper.toEntity(dto);

        Set<Role> roles = new HashSet<>();
        for (Integer id : dto.getRoleIds()) {
            if (roleRepository.findById(id).isPresent()) {
                roles.add(roleRepository.findById(id).get());
            }
        }

        user.setAvatarUrl(avatar);
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setRoles(roles);

        User savedUser = userRepository.save(user);

        return userMapper.toDto(savedUser);
    }

    @Override
    public UserResponseDTO update(UserUpdateDTO dto, MultipartFile multipartFile) {
        User user = userRepository.findById(dto.getId()).orElseThrow(
                () -> new UserNotFoundException("User not found with id: " + dto.getId()));

        Optional<User> existing = userRepository.findByEmail(dto.getEmail());
        if (existing.isPresent() && !existing.get().getId().equals(dto.getId())) {
            throw new EmailAlreadyExistsException("Email already in use by another user");
        }

        userMapper.updateEntityFromDto(dto, user);

        if (multipartFile != null && !multipartFile.isEmpty()) {
            log.info("Uploading avatar for admin {}", dto.getId());
            String avatarUrl = assetService.uploadToCloudinary(multipartFile, "avatar");
            user.setAvatarUrl(avatarUrl);
        }


        if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(dto.getPassword()));
        }

        if (dto.getRoleIds() != null && !dto.getRoleIds().isEmpty()) {
            Set<Role> roles = dto.getRoleIds().stream()
                    .map(roleId -> roleRepository.findById(roleId)
                            .orElseThrow(() -> new RoleNotFoundException("Role not found: " + roleId)))
                    .collect(Collectors.toSet());
            user.setRoles(roles);
        }

        User savedUser = userRepository.save(user);

        return userMapper.toDto(savedUser);
    }

    @Override
    public UserResponseDTO findById(Integer id) {

        User user = userRepository.findById(id).orElseThrow(
                () -> new UserNotFoundException("User not found with id: " + id)
        );


        return userMapper.toDto(user);
    }

    @Override
    public void changeEnabled(Integer id, boolean enabled) {

        User user = userRepository.findById(id).orElseThrow(
                () -> new UserNotFoundException("User not found with id: " + id)
        );
        user.setEnabled(enabled);
        userRepository.save(user);
    }

    @Override
    public void delete(Integer id, Integer userLoginId) {
        User user = userRepository.findById(id).orElseThrow(
                () -> new UserNotFoundException("User not found with id: " + id)
        );
        user.setDeleted(true);
        user.setDeletedAt(new Date());
        user.setDeletedById(userLoginId);

        userRepository.save(user);
    }

    @Override
    public PageResponseDTO<UserResponseDTO> getAllUser(int page, int size, String sortField, String sortDir, String keyword) {

        Sort sort = sortDir.equalsIgnoreCase("desc")
                ? Sort.by(sortField).descending()
                : Sort.by(sortField).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);

        Page<User> userPage = userRepository.search(keyword, pageable);

        List<UserResponseDTO> users = userPage.getContent().stream()
                .map(user -> userMapper.toDto(user)).toList();
        return new PageResponseDTO<>(
                users,
                userPage.getNumber(),
                userPage.getSize(),
                userPage.getTotalElements(),
                userPage.getTotalPages()
        );
    }
}
