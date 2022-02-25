package com.awesome.awesomenotes.user;

import java.util.Set;

import com.awesome.awesomenotes.authentication.AuthException;
import com.awesome.awesomenotes.authentication.AuthService;
import com.awesome.awesomenotes.exception.LackOfPermissionsException;
import com.awesome.awesomenotes.user.role.ERole;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController {
    @Autowired
    UserService userService;
    @Autowired
    UserConverter userConverter;

    @PatchMapping(path = "/{id}/roles")
    public String registerUser(@RequestBody Set<ERole> roles,
            @RequestAttribute(name = "user") User authorizedUser,
            @PathVariable(name = "id") Long userId) throws AuthException, LackOfPermissionsException {
        AuthService.requireAnyRole(authorizedUser, ERole.ROLE_ADMIN);
        userService.changeUsersRoles(roles, userId);
        return "Roles were changed";
    }

}
