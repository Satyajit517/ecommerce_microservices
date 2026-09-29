package com.project.UserService.Controller;


import com.project.UserService.DTO.ApiResponse;
import com.project.UserService.DTO.User.UserResponse;
import com.project.UserService.DTO.User.UserUpdateRequest;
import com.project.UserService.Service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> getCurrentUser()
    {
        UserResponse response = userService.getCurrentUser();

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "User fetched successfully",
                        response,
                        Instant.now()
                )
        );
    }

    @PutMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> updateProfile(@Valid @RequestBody
                                                                       UserUpdateRequest request)
    {
        UserResponse response = userService.updateUser(request);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Profile Updated successfully",
                        response,
                        Instant.now()
                )
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> getUserById(@PathVariable UUID id)
    {
        UserResponse response = userService.getUserById(id);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "User fetched successfully",
                        response,
                        Instant.now()
                )
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> deleteUser(@PathVariable UUID id)
    {
        userService.deleteUser(id);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "User deleted successfully",
                        null,
                        Instant.now()
                )
        );
    }
}
