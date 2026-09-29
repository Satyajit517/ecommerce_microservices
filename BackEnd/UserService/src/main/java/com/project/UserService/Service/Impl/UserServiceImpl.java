package com.project.UserService.Service.Impl;

import com.project.UserService.DTO.User.SellerRegisterRequest;
import com.project.UserService.DTO.User.UserRegisterRequest;
import com.project.UserService.DTO.User.UserResponse;
import com.project.UserService.DTO.User.UserUpdateRequest;
import com.project.UserService.Entity.User;
import com.project.UserService.Enum.Role;
import com.project.UserService.Enum.UserStatus;
import com.project.UserService.Exception.ConflictException;
import com.project.UserService.Exception.ResourceNotFoundException;
import com.project.UserService.Mapper.UserMapper;
import com.project.UserService.Repository.UserRepository;
import com.project.UserService.Service.CurrentUserService;
import com.project.UserService.Service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final CurrentUserService currentUserService;

    @Override
    public UserResponse register(UserRegisterRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ConflictException("Email already registered");
        }

        User user = new User();

        user.setName(request.getName());
        user.setEmail(request.getEmail());

        user.setPasswordHash(
                passwordEncoder.encode(request.getPassword())
        );

        user.setRole(Role.BUYER);
        user.setStatus(UserStatus.ACTIVE);

        Instant now = Instant.now();

        user.setCreatedAt(now);
        user.setUpdatedAt(now);

        User savedUser = userRepository.save(user);

        return userMapper.toResponse(savedUser);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Override
    public UserResponse getUserById(UUID id) {

        User user = userRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found")
                );

        return userMapper.toResponse(user);
    }

    @Override
    public UserResponse getCurrentUser() {

        User user = currentUserService.getCurrentUser();

        return userMapper.toResponse(user);
    }

    @Override
    @Transactional
    public UserResponse updateUser(UserUpdateRequest request)
    {
        User user = currentUserService.getCurrentUser();

        if(request.getName() != null)
        {
            user.setName(request.getName());
        }
        if(request.getPhones() != null)
        {
            user.setPhones(request.getPhones());
        }

        user.setUpdatedAt(Instant.now());

        User updatedUser = userRepository.save(user);

        return userMapper.toResponse(updatedUser);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Override
    public void deleteUser(UUID id) {

        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User not found");
        }

        userRepository.deleteById(id);
    }

    @Override
    public UserResponse registerSeller(SellerRegisterRequest request){

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ConflictException("Email already registered");
        }

        User user = new User();

        user.setName(request.getName());
        user.setEmail(request.getEmail());

        user.setPasswordHash(
                passwordEncoder.encode(request.getPassword())
        );

        user.setRole(Role.SELLER);
        user.setStatus(UserStatus.ACTIVE);

        Instant now = Instant.now();

        user.setCreatedAt(now);
        user.setUpdatedAt(now);

        User savedUser = userRepository.save(user);

        return userMapper.toResponse(savedUser);
    }
}