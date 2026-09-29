package com.project.UserService.Mapper;


import com.project.UserService.DTO.Address.AddressCreateRequest;
import com.project.UserService.DTO.Address.AddressResponse;
import com.project.UserService.Entity.Address;
import org.springframework.stereotype.Component;

@Component
public class AddressMapper {
    public AddressResponse toResponse(Address address)
    {
        AddressResponse response = new AddressResponse();

        response.setId(address.getId());
        response.setAddressLine(address.getAddressLine());
        response.setCity(address.getCity());
        response.setState(address.getState());
        response.setAddressType(address.getAddressType());
        response.setPostalCode(address.getPostalCode());
        response.setDefault(address.isDefault());

        return response;
    }

    public Address toEntity(AddressCreateRequest request) {

        Address address = new Address();

        address.setAddressLine(request.getAddressLine());
        address.setCity(request.getCity());
        address.setState(request.getState());
        address.setAddressType(request.getAddressType());
        address.setPostalCode(request.getPostalCode());
        address.setDefault(request.isDefault());

        return address;
    }
}
