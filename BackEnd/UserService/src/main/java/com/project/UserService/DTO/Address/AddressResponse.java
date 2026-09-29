package com.project.UserService.DTO.Address;

import com.project.UserService.Enum.AddressType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AddressResponse {

    private UUID id;

    private String addressLine;

    private String city;

    private String state;

    private String postalCode;

    private AddressType addressType;

    private boolean isDefault;
}