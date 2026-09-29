package com.project.UserService.Mapper;


import com.project.UserService.DTO.User.UserResponse;
import com.project.UserService.Entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserMapper {

    private final AddressMapper addressMapper;

    public UserResponse toResponse(User user) {

        UserResponse response = new UserResponse();

        response.setId(user.getId());
        response.setName(user.getName());
        response.setEmail(user.getEmail());
        response.setStatus(user.getStatus());
        response.setRole(user.getRole());
        response.setPhones(user.getPhones());

        if(!user.getAddresses().isEmpty())
        {
            response.setAddresses(
                    user.getAddresses()
                            .stream()
                            .map(addressMapper::toResponse)
                            .toList()
            );
        }

        response.setCreatedAt(user.getCreatedAt());
        response.setUpdatedAt(user.getUpdatedAt());

        return response;
    }
}