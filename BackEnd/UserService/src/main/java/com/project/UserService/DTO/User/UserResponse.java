package com.project.UserService.DTO.User;


import com.project.UserService.DTO.Address.AddressResponse;
import com.project.UserService.Enum.Role;
import com.project.UserService.Enum.UserStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
public class UserResponse {

    private UUID id;

    private String name;

    private String email;

    private UserStatus status;

    private Role role;

    private Set<String> phones;

    private List<AddressResponse> addresses;

    private Instant createdAt;

    private Instant updatedAt;
}