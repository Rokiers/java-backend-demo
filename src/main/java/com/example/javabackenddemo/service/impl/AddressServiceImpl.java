package com.example.javabackenddemo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.javabackenddemo.dto.request.CreateAddressRequest;
import com.example.javabackenddemo.dto.request.UpdateAddressRequest;
import com.example.javabackenddemo.dto.response.AddressResponse;
import com.example.javabackenddemo.entity.UserAddress;
import com.example.javabackenddemo.exception.ResourceNotFoundException;
import com.example.javabackenddemo.mapper.UserAddressMapper;
import com.example.javabackenddemo.service.AddressService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AddressServiceImpl implements AddressService {

    private final UserAddressMapper addressMapper;

    public AddressServiceImpl(UserAddressMapper addressMapper) {
        this.addressMapper = addressMapper;
    }

    @Override
    public List<AddressResponse> listAddresses(Long userId) {
        return addressMapper.selectList(new LambdaQueryWrapper<UserAddress>()
                .eq(UserAddress::getUserId, userId)
                .orderByDesc(UserAddress::getIsDefault)
                .orderByDesc(UserAddress::getCreatedAt))
                .stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional
    public AddressResponse createAddress(Long userId, CreateAddressRequest request) {
        if (Boolean.TRUE.equals(request.isDefault())) {
            clearDefault(userId);
        }
        UserAddress address = UserAddress.builder()
                .userId(userId)
                .receiverName(request.receiverName())
                .receiverPhone(request.receiverPhone())
                .province(request.province())
                .city(request.city())
                .district(request.district())
                .detailAddress(request.detailAddress())
                .isDefault(Boolean.TRUE.equals(request.isDefault()))
                .build();
        addressMapper.insert(address);
        return toResponse(address);
    }

    @Override
    @Transactional
    public AddressResponse updateAddress(Long userId, Long addressId, UpdateAddressRequest request) {
        UserAddress address = addressMapper.selectOne(new LambdaQueryWrapper<UserAddress>()
                .eq(UserAddress::getId, addressId).eq(UserAddress::getUserId, userId));
        if (address == null) throw new ResourceNotFoundException("Address not found: " + addressId);
        if (request.receiverName() != null) address.setReceiverName(request.receiverName());
        if (request.receiverPhone() != null) address.setReceiverPhone(request.receiverPhone());
        if (request.province() != null) address.setProvince(request.province());
        if (request.city() != null) address.setCity(request.city());
        if (request.district() != null) address.setDistrict(request.district());
        if (request.detailAddress() != null) address.setDetailAddress(request.detailAddress());
        if (Boolean.TRUE.equals(request.isDefault())) {
            clearDefault(userId);
            address.setIsDefault(true);
        }
        addressMapper.updateById(address);
        return toResponse(address);
    }

    @Override
    public void deleteAddress(Long userId, Long addressId) {
        UserAddress address = addressMapper.selectOne(new LambdaQueryWrapper<UserAddress>()
                .eq(UserAddress::getId, addressId).eq(UserAddress::getUserId, userId));
        if (address == null) throw new ResourceNotFoundException("Address not found: " + addressId);
        addressMapper.deleteById(address.getId());
    }

    @Override
    @Transactional
    public AddressResponse setDefault(Long userId, Long addressId) {
        UserAddress address = addressMapper.selectOne(new LambdaQueryWrapper<UserAddress>()
                .eq(UserAddress::getId, addressId).eq(UserAddress::getUserId, userId));
        if (address == null) throw new ResourceNotFoundException("Address not found: " + addressId);
        clearDefault(userId);
        address.setIsDefault(true);
        addressMapper.updateById(address);
        return toResponse(address);
    }

    private void clearDefault(Long userId) {
        UserAddress def = addressMapper.selectOne(new LambdaQueryWrapper<UserAddress>()
                .eq(UserAddress::getUserId, userId).eq(UserAddress::getIsDefault, true));
        if (def != null) {
            def.setIsDefault(false);
            addressMapper.updateById(def);
        }
    }

    private AddressResponse toResponse(UserAddress a) {
        return new AddressResponse(a.getId(), a.getReceiverName(), a.getReceiverPhone(),
                a.getProvince(), a.getCity(), a.getDistrict(), a.getDetailAddress(),
                a.getIsDefault(), a.getCreatedAt());
    }
}
