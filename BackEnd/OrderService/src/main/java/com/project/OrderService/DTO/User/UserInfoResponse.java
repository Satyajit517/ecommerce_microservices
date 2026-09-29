package com.project.OrderService.DTO.User;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class UserInfoResponse {

    private UUID id;
    private String name;
    private String email;
}