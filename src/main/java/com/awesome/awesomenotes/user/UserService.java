package com.awesome.awesomenotes.user;

import java.util.Set;

import com.awesome.awesomenotes.user.role.ERole;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired
    UserRepository userRepository;

    public User changeUsersRoles(Set<ERole> roles, Long userId) {
        User updatedUser = new User();
        updatedUser.setId(userId);
        updatedUser.setRoles(roles);
        return userRepository.save(updatedUser);
    }
}
