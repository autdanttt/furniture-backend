package org.frogcy.furnitureadmin.user.impl;

import org.frogcy.furnitureadmin.media.AssetService;
import org.frogcy.furnitureadmin.user.RoleRepository;
import org.frogcy.furnitureadmin.user.UserRepository;
import org.frogcy.furnitureadmin.user.UserService;
import org.frogcy.furnitureadmin.user.dto.UserCreateDTO;
import org.frogcy.furnitureadmin.user.dto.UserMapper;
import org.frogcy.furnitureadmin.user.dto.UserResponseDTO;
import org.frogcy.furniturecommon.entity.Role;
import org.frogcy.furniturecommon.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
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
        user.setEnabled(true);
        user.setRoles(roles);

        User savedUser = userRepository.save(user);

        return userMapper.toDto(savedUser);
    }
}
