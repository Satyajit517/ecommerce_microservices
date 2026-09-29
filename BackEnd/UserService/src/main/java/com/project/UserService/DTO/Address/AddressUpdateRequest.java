package com.project.UserService.DTO.Address;

import com.project.UserService.Enum.AddressType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddressUpdateRequest {

    @NotBlank(message = "Address line is required")
    @Size(max = 255, message = "Address line cannot exceed 255 characters")
    private String addressLine;

    @NotBlank(message = "City is required")
    private String city;

    @NotBlank(message = "State is required")
    private String state;

    @NotNull(message = "Address type is required")
    private AddressType addressType;

    @NotBlank(message = "Postal code is required")
    private String postalCode;

    private boolean isDefault;
}