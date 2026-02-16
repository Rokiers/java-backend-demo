package com.example.javabackenddemo.controller.api;

import com.example.javabackenddemo.dto.request.AddCartItemRequest;
import com.example.javabackenddemo.dto.request.UpdateCartItemRequest;
import com.example.javabackenddemo.dto.response.ApiResponse;
import com.example.javabackenddemo.dto.response.CartResponse;
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

    // Simplified: using header X-User-Id to identify user
    @GetMapping
    public ApiResponse<CartResponse> getCart(@RequestHeader("X-User-Id") Long userId,
            @RequestParam(required = false) String currency) {
        return ApiResponse.success(cartService.getCart(userId, currency));
    }

    @PostMapping("/items")
    public ApiResponse<CartResponse> addItem(@RequestHeader("X-User-Id") Long userId,
            @Valid @RequestBody AddCartItemRequest request) {
        return ApiResponse.success(cartService.addItem(userId, request));
    }

    @PutMapping("/items/{itemId}")
    public ApiResponse<CartResponse> updateItem(@RequestHeader("X-User-Id") Long userId,
            @PathVariable Long itemId, @Valid @RequestBody UpdateCartItemRequest request) {
        return ApiResponse.success(cartService.updateItemQuantity(userId, itemId, request.quantity()));
    }

    @DeleteMapping("/items/{itemId}")
    public ApiResponse<CartResponse> removeItem(@RequestHeader("X-User-Id") Long userId,
            @PathVariable Long itemId) {
        return ApiResponse.success(cartService.removeItem(userId, itemId));
    }
}
