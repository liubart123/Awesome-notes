package com.awesome.awesomenotes.user;

import java.util.HashSet;
import java.util.Set;

import javax.validation.constraints.*;

import com.awesome.awesomenotes.user.role.ERole;

import lombok.*;

public abstract class UserDto {
    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UserCreationRequest {
        @NotBlank
        private String username;
        @NotBlank
        @Size(max = 50)
        @Email
        private String email;
        @NotBlank
        @Size(max = 120)
        private String password;
        private Set<ERole> roles = new HashSet<>();
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UserLoginRequest {
        @NotBlank
        private String email;
        @NotBlank
        @Size(max = 120)
        private String password;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UserResponse {
        private Long Id;
        private String username;
        private String email;
        private Set<ERole> roles = new HashSet<>();
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UserLoginResponse {
        private Long Id;
        private String username;
        private String email;
        private Set<ERole> roles = new HashSet<>();
        private String token;
    }

}
