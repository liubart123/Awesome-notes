package com.awesome.awesomenotes.user;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.Setter;

@Component
@Setter
public class UserConverter {

    @Autowired
    ModelMapper modelMapper;

    public User convert(UserDto.UserCreationRequest dto) {
        User user = modelMapper.map(dto, User.class);
        return user;
    }

    public User convert(UserDto.UserLoginRequest dto) {
        User user = modelMapper.map(dto, User.class);
        return user;
    }

    public UserDto.UserResponse convert(User user) {
        UserDto.UserResponse dto = modelMapper.map(user, UserDto.UserResponse.class);
        return dto;
    }

    public UserDto.UserLoginResponse convertToLoginResponse(User user) {
        UserDto.UserLoginResponse dto = modelMapper.map(user, UserDto.UserLoginResponse.class);
        return dto;
    }
}
