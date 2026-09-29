package com.project.UserService.Service;

import com.project.UserService.DTO.User.SellerRegisterRequest;
import com.project.UserService.DTO.User.UserRegisterRequest;
import com.project.UserService.DTO.User.UserResponse;
import com.project.UserService.DTO.User.UserUpdateRequest;

import java.util.UUID;

public interface UserService {

    UserResponse register(UserRegisterRequest request);

    UserResponse getUserById(UUID id);

    UserResponse getCurrentUser();

    UserResponse updateUser(UserUpdateRequest request);

    void deleteUser(UUID id);

    UserResponse registerSeller(SellerRegisterRequest request);
}