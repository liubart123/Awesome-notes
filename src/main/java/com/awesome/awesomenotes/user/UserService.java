package com.awesome.awesomenotes.user;

import java.util.Set;

import com.awesome.awesomenotes.exception.ElementCreationException;
import com.awesome.awesomenotes.exception.ElementNotFoundException;
import com.awesome.awesomenotes.user.role.ERole;
import com.awesome.awesomenotes.util.CryptoUtil;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Service
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserService {

    private final String INVALID_CREDENTIALS = "Invalid creadentials";

    @Autowired
    UserRepository userRepository;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    CryptoUtil cryptoUtil;

    public User registerUser(User user) throws ElementCreationException {
        User savedUser = user.clone();
        savedUser.setPassword(cryptoUtil.hashString(savedUser.getPassword()));
        if (savedUser.getRoles().size() == 0) {
            savedUser.getRoles().add(ERole.ROLE_USER);
        }
        if (userRepository.existsByEmail(savedUser.getEmail()))
            throw new ElementCreationException("User with email: " + savedUser.getEmail() + " already exists.");
        if (userRepository.existsByUsername(savedUser.getUsername()))
            throw new ElementCreationException("User with username: " + savedUser.getUsername() + " already exists.");
        return userRepository.save(savedUser);
    }

    public User findUserByCreds(User user) throws ElementNotFoundException {
        User foundedUser = userRepository
                .findByEmail(user.getEmail())
                .orElseThrow(
                        () -> new ElementNotFoundException(
                                INVALID_CREDENTIALS));
        String hashedPassword = cryptoUtil.hashString(user.getPassword());
        if (!hashedPassword.equals(foundedUser.getPassword()))
            throw new ElementNotFoundException(
                    INVALID_CREDENTIALS);
        return foundedUser;
    }

    public User findUserByEmail(String email) throws ElementNotFoundException {
        User foundedUser = userRepository
                .findByEmail(email)
                .orElseThrow(
                        () -> new ElementNotFoundException(
                                "User doesn't exist with such email: " + email));
        return foundedUser;
    }

    public User changeUsersRoles(Set<ERole> roles, Long userId) {
        User updatedUser = userRepository.getById(userId);
        updatedUser.setRoles(roles);
        return userRepository.save(updatedUser);
    }
}
