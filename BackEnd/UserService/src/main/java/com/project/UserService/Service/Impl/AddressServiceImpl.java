package com.project.UserService.Service.Impl;


import com.project.UserService.DTO.Address.AddressCreateRequest;
import com.project.UserService.DTO.Address.AddressResponse;
import com.project.UserService.DTO.Address.AddressUpdateRequest;
import com.project.UserService.Entity.Address;
import com.project.UserService.Entity.User;
import com.project.UserService.Exception.ResourceNotFoundException;
import com.project.UserService.Mapper.AddressMapper;
import com.project.UserService.Repository.AddressRepository;
import com.project.UserService.Service.AddressService;
import com.project.UserService.Service.CurrentUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AddressServiceImpl implements AddressService {

    private final AddressRepository addressRepository;

    private final AddressMapper addressMapper;

    private final CurrentUserService currentUserService;

    @Override
    @Transactional
    public AddressResponse createAddress(AddressCreateRequest request) {

        User user = currentUserService.getCurrentUser();

        Address address = addressMapper.toEntity(request);
        address.setUser(user);

        if(addressRepository.findByUser(user).isEmpty()){
            address.setDefault(true);
        }

        if(address.isDefault())
        {
            removeExistingDefaultAddress(user);
        }

        Address savedAddress = addressRepository.save(address);

        return addressMapper.toResponse(savedAddress);
    }



    @Override
    @Transactional(readOnly = true)
    public List<AddressResponse> getMyAddresses() {

        User user = currentUserService.getCurrentUser();

        return addressRepository.findByUser(user)
                .stream()
                .map(addressMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public AddressResponse getMyAddress(UUID addressId) {

        User user = currentUserService.getCurrentUser();

        Address address =  addressRepository
                .findByIdAndUser(addressId, user)
                .orElseThrow(() -> new ResourceNotFoundException("Address Not Found"));

        return addressMapper.toResponse(address);
    }

    @Override
    @Transactional
    public AddressResponse updateAddress(UUID addressId, AddressUpdateRequest request) {

        User user = currentUserService.getCurrentUser();

        Address address = addressRepository
                .findByIdAndUser(addressId, user)
                .orElseThrow(()-> new ResourceNotFoundException("Address does not found"));

        address.setAddressLine(request.getAddressLine());
        address.setAddressType(request.getAddressType());
        address.setCity(request.getCity());
        address.setState(request.getState());
        address.setPostalCode(request.getPostalCode());

        if(request.isDefault())
        {
            removeExistingDefaultAddress(user);
            address.setDefault(true);
        }
        else{
            address.setDefault(false);
        }

        return addressMapper.toResponse(addressRepository.save(address));
    }

    @Override
    @Transactional
    public void deleteAddress(UUID addressId) {

        User user = currentUserService.getCurrentUser();

        Address address = addressRepository
                .findByIdAndUser(addressId, user)
                .orElseThrow( ()-> new ResourceNotFoundException("Address not found"));

        boolean wasDefault = address.isDefault();

        addressRepository.delete(address);

        if(wasDefault)
        {
            List<Address> remaining = addressRepository.findByUser(user);

            if(!remaining.isEmpty())
            {
                remaining.get(0).setDefault(true);
                addressRepository.save(remaining.get(0));
            }
        }
    }

    @Override
    @Transactional
    public AddressResponse setDefaultAddress(UUID addressId) {

        User user = currentUserService.getCurrentUser();

        Address address = addressRepository.findByIdAndUser(addressId, user)
                .orElseThrow(() -> new ResourceNotFoundException("Address not found"));

        removeExistingDefaultAddress(user);

        address.setDefault(true);

        return addressMapper.toResponse(address);
    }



    private void removeExistingDefaultAddress(User user) {

        List<Address> addresssList = addressRepository.findByUser(user);

        addresssList.stream()
                .filter(Address::isDefault)
                .forEach(address -> {
                    address.setDefault(false);
                    addressRepository.save(address);
                });
    }

    @Override
    @Transactional(readOnly = true)
    public AddressResponse getAddressForUser(UUID userId, UUID addressId) {

        Address address = addressRepository
                .findByIdAndUserId(addressId, userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Address not found for this user"
                        ));

        return addressMapper.toResponse(address);
    }

}
