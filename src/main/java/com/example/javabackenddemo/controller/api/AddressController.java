package com.example.javabackenddemo.controller.api;

import com.example.javabackenddemo.dto.request.CreateAddressRequest;
import com.example.javabackenddemo.dto.request.UpdateAddressRequest;
import com.example.javabackenddemo.dto.response.AddressResponse;
import com.example.javabackenddemo.dto.response.ApiResponse;
import com.example.javabackenddemo.security.SecurityUtils;
import com.example.javabackenddemo.service.AddressService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/addresses")
public class AddressController {

    private final AddressService addressService;

    public AddressController(AddressService addressService) {
        this.addressService = addressService;
    }

    @GetMapping
    public ApiResponse<List<AddressResponse>> list() {
        return ApiResponse.success(addressService.listAddresses(SecurityUtils.getCurrentUserId()));
    }

    @PostMapping
    public ApiResponse<AddressResponse> create(@Valid @RequestBody CreateAddressRequest request) {
        return ApiResponse.success(addressService.createAddress(SecurityUtils.getCurrentUserId(), request));
    }

    @PutMapping("/{id}")
    public ApiResponse<AddressResponse> update(@PathVariable Long id, @Valid @RequestBody UpdateAddressRequest request) {
        return ApiResponse.success(addressService.updateAddress(SecurityUtils.getCurrentUserId(), id, request));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        addressService.deleteAddress(SecurityUtils.getCurrentUserId(), id);
        return ApiResponse.success(null);
    }

    @PutMapping("/{id}/default")
    public ApiResponse<AddressResponse> setDefault(@PathVariable Long id) {
        return ApiResponse.success(addressService.setDefault(SecurityUtils.getCurrentUserId(), id));
    }
}
