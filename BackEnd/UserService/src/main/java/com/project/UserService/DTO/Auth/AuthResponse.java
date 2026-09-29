package com.project.UserService.DTO.Auth;

import com.project.UserService.DTO.User.UserResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AuthResponse {

    private String accessToken;

    private UserResponse user;
}