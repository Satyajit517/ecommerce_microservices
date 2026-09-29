package com.project.OrderService.DTO.Order;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class OrderAddressResponse {

    private UUID id;

    private String addressLine;

    private String city;

    private String state;

    private String postalCode;
}