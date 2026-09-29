package com.project.UserService.Service;

import com.project.UserService.DTO.Auth.AuthTokenResult;
import com.project.UserService.DTO.User.SellerRegisterRequest;
import com.project.UserService.DTO.User.UserLoginRequest;
import com.project.UserService.DTO.User.UserRegisterRequest;
import com.project.UserService.DTO.User.UserResponse;

public interface AuthService {

    UserResponse register(UserRegisterRequest request);

    AuthTokenResult login(UserLoginRequest request);

    AuthTokenResult refreshToken(String refreshToken);

    void logout(String refreshToken);

    UserResponse registerSeller(SellerRegisterRequest request);
}