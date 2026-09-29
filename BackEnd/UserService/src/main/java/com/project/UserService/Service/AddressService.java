package com.project.UserService.Service;



import com.project.UserService.DTO.Address.AddressCreateRequest;
import com.project.UserService.DTO.Address.AddressResponse;
import com.project.UserService.DTO.Address.AddressUpdateRequest;

import java.util.List;
import java.util.UUID;

public interface AddressService {

    AddressResponse createAddress(AddressCreateRequest request);

    List<AddressResponse> getMyAddresses();

    AddressResponse getMyAddress(UUID addressId);

    AddressResponse updateAddress(UUID addressId, AddressUpdateRequest request);

    void deleteAddress(UUID addressId);

    AddressResponse setDefaultAddress(UUID addressId);

    AddressResponse getAddressForUser(UUID userId, UUID addressId);
}