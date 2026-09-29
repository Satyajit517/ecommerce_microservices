package com.project.UserService.DTO.Auth;

import com.project.UserService.DTO.User.UserResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AuthTokenResult {

    private String accessToken;

    private String refreshToken;

    private UserResponse user;
}