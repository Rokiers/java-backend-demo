package com.example.javabackenddemo.controller.api;

import com.example.javabackenddemo.dto.request.AddCartItemRequest;
import com.example.javabackenddemo.dto.request.UpdateCartItemRequest;
import com.example.javabackenddemo.dto.response.ApiResponse;
import com.example.javabackenddemo.dto.response.CartResponse;
import com.example.javabackenddemo.security.SecurityUtils;
import com.example.javabackenddemo.service.CartService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/cart")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping
    public ApiResponse<CartResponse> getCart(@RequestParam(required = false) String currency) {
        return ApiResponse.success(cartService.getCart(SecurityUtils.getCurrentUserId(), currency));
    }

    @PostMapping("/items")
    public ApiResponse<CartResponse> addItem(@Valid @RequestBody AddCartItemRequest request) {
        return ApiResponse.success(cartService.addItem(SecurityUtils.getCurrentUserId(), request));
    }

    @PutMapping("/items/{itemId}")
    public ApiResponse<CartResponse> updateItem(@PathVariable Long itemId, @Valid @RequestBody UpdateCartItemRequest request) {
        return ApiResponse.success(cartService.updateItemQuantity(SecurityUtils.getCurrentUserId(), itemId, request.quantity()));
    }

    @DeleteMapping("/items/{itemId}")
    public ApiResponse<CartResponse> removeItem(@PathVariable Long itemId) {
        return ApiResponse.success(cartService.removeItem(SecurityUtils.getCurrentUserId(), itemId));
    }
}
