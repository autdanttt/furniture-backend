package org.frogcy.furnitureadmin.user;

import org.frogcy.furniturecommon.entity.User;
import org.springframework.stereotype.Service;

@Service
public class UserService {


    private UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User getByEmail(String email){
        return userRepository.getUserByEmail(email);
    }
}
