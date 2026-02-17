package com.example.javabackenddemo.controller.api;

import com.example.javabackenddemo.dto.request.CreateAddressRequest;
import com.example.javabackenddemo.dto.request.UpdateAddressRequest;
import com.example.javabackenddemo.dto.response.AddressResponse;
import com.example.javabackenddemo.dto.response.ApiResponse;
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
    public ApiResponse<List<AddressResponse>> list(@RequestHeader("X-User-Id") Long userId) {
        return ApiResponse.success(addressService.listAddresses(userId));
    }

    @PostMapping
    public ApiResponse<AddressResponse> create(@RequestHeader("X-User-Id") Long userId,
            @Valid @RequestBody CreateAddressRequest request) {
        return ApiResponse.success(addressService.createAddress(userId, request));
    }

    @PutMapping("/{id}")
    public ApiResponse<AddressResponse> update(@RequestHeader("X-User-Id") Long userId,
            @PathVariable Long id, @Valid @RequestBody UpdateAddressRequest request) {
        return ApiResponse.success(addressService.updateAddress(userId, id, request));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@RequestHeader("X-User-Id") Long userId, @PathVariable Long id) {
        addressService.deleteAddress(userId, id);
        return ApiResponse.success(null);
    }

    @PutMapping("/{id}/default")
    public ApiResponse<AddressResponse> setDefault(@RequestHeader("X-User-Id") Long userId,
            @PathVariable Long id) {
        return ApiResponse.success(addressService.setDefault(userId, id));
    }
}
