package com.awesome.awesomenotes.authentication;

import javax.validation.Valid;

import com.awesome.awesomenotes.exception.ElementCreationException;
import com.awesome.awesomenotes.exception.ElementNotFoundException;
import com.awesome.awesomenotes.exception.LackOfPermissionsException;
import com.awesome.awesomenotes.user.User;
import com.awesome.awesomenotes.user.UserConverter;
import com.awesome.awesomenotes.user.UserDto;
import com.awesome.awesomenotes.user.UserService;
import com.awesome.awesomenotes.user.role.ERole;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    AuthService authService;

    @Autowired
    UserConverter userConverter;
    @Autowired
    UserService userService;

    @PostMapping(path = "/register")
    @ResponseStatus(HttpStatus.CREATED)
    public String registerUser(@Valid @RequestBody UserDto.UserCreationRequest user) throws ElementCreationException {
        userService.registerUser(userConverter.convert(user));
        return "User was created";
    }

    @PostMapping(path = "/login")
    public UserDto.UserLoginResponse login(@Valid @RequestBody UserDto.UserLoginRequest user)
            throws AuthException {
        try {
            User realUser = userService.findUserByCreds(userConverter.convert(user));
            String jwt = authService.generateJwt(realUser);
            UserDto.UserLoginResponse response = userConverter.convertToLoginResponse(realUser);
            response.setToken(jwt);
            return response;
        } catch (ElementNotFoundException e) {
            throw new AuthException(e.getMessage(), HttpStatus.UNAUTHORIZED);
        }
    }

    @GetMapping(path = "/current")
    public UserDto.UserResponse getCurrentUser(@RequestAttribute(name = "user") User user)
            throws ElementNotFoundException {
        return userConverter.convert(userService.findUserByEmail(user.getEmail()));
    }

    @GetMapping(path = "/admin")
    public void getForAdmin(@RequestAttribute(name = "user") User user)
            throws ElementNotFoundException, AuthException, LackOfPermissionsException {
        AuthService.requireAnyRole(user, ERole.ROLE_ADMIN);
    }

    @GetMapping(path = "/moderator")
    public void getForModerator(@RequestAttribute(name = "user") User user)
            throws ElementNotFoundException, AuthException, LackOfPermissionsException {
        AuthService.requireAnyRole(user, ERole.ROLE_MODERATOR);
    }

}
