package com.project.UserService.Controller;


import com.project.UserService.DTO.Address.AddressCreateRequest;
import com.project.UserService.DTO.Address.AddressResponse;
import com.project.UserService.DTO.Address.AddressUpdateRequest;
import com.project.UserService.DTO.ApiResponse;
import com.project.UserService.Service.AddressService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/address")
@RequiredArgsConstructor
public class AddressController {

    private final AddressService addressService;

    @PostMapping
    public ResponseEntity<ApiResponse<AddressResponse>> createAddress(@Valid @RequestBody AddressCreateRequest request)
    {

        AddressResponse response = addressService.createAddress(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(
                new ApiResponse<>(
                        true,
                        "Address created successfully",
                        response, Instant.now()
                )
        );
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<AddressResponse>>> getMyAddress()
    {
        List<AddressResponse> response = addressService.getMyAddresses();

        return ResponseEntity.ok(new ApiResponse<>(
                true,
                "Address fetched successfully",
                response,
                Instant.now()
            )
        );
    }

    @GetMapping("/{addressId}")
    public ResponseEntity<ApiResponse<AddressResponse>> getMyAddress(
            @PathVariable UUID addressId) {

        AddressResponse response = addressService.getMyAddress(addressId);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Address fetched successfully",
                        response,
                        Instant.now()
                )
        );
    }

    @PutMapping("/{addressId}")
    public ResponseEntity<ApiResponse<AddressResponse>> updateAddress(
            @PathVariable UUID addressId,
            @Valid @RequestBody AddressUpdateRequest request) {

        AddressResponse response =
                addressService.updateAddress(addressId, request);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Address updated successfully",
                        response,
                        Instant.now()
                )
        );
    }

    @DeleteMapping("/{addressId}")
    public ResponseEntity<ApiResponse<Void>> deleteAddress(
            @PathVariable UUID addressId) {

        addressService.deleteAddress(addressId);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Address deleted successfully",
                        null,
                        Instant.now()
                )
        );
    }

    @PutMapping("/{addressId}/default")
    public ResponseEntity<ApiResponse<AddressResponse>> setDefaultAddress(
            @PathVariable UUID addressId) {

        AddressResponse response =
                addressService.setDefaultAddress(addressId);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Default address updated successfully",
                        response,
                        Instant.now()
                )
        );
    }

    @GetMapping("/user/{userId}/{addressId}")
    public ResponseEntity<ApiResponse<AddressResponse>> getAddressForUser(
            @PathVariable UUID userId,
            @PathVariable UUID addressId) {

        AddressResponse response =
                addressService.getAddressForUser(userId, addressId);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Address fetched successfully",
                        response,
                        Instant.now()
                )
        );
    }

}
