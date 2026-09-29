package com.project.OrderService.DTO.User;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class AddressInfoResponse {

    private UUID id;
    private String addressLine;
    private String city;
    private String state;
    private String postalCode;
    private String addressType;
    private boolean isDefault;
}