package com.project.OrderService.Service;

import com.project.OrderService.DTO.ApiResponse;
import com.project.OrderService.DTO.User.AddressInfoResponse;
import com.project.OrderService.DTO.User.UserInfoResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "USER-SERVICE")
public interface UserClient {

    @GetMapping("/api/users/{id}")
    ApiResponse<UserInfoResponse> getUser(
            @PathVariable UUID id
    );

    @GetMapping("/api/address/user/{userId}/{addressId}")
    ApiResponse<AddressInfoResponse> getAddressForUser(
            @PathVariable UUID userId,
            @PathVariable UUID addressId
    );
}