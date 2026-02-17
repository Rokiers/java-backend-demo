package com.example.javabackenddemo.service;

import com.example.javabackenddemo.dto.request.CreateAddressRequest;
import com.example.javabackenddemo.dto.request.UpdateAddressRequest;
import com.example.javabackenddemo.dto.response.AddressResponse;

import java.util.List;

public interface AddressService {
    List<AddressResponse> listAddresses(Long userId);
    AddressResponse createAddress(Long userId, CreateAddressRequest request);
    AddressResponse updateAddress(Long userId, Long addressId, UpdateAddressRequest request);
    void deleteAddress(Long userId, Long addressId);
    AddressResponse setDefault(Long userId, Long addressId);
}
