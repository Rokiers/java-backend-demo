package com.example.javabackenddemo.controller.api;

import com.example.javabackenddemo.dto.response.ApiResponse;
import com.example.javabackenddemo.dto.response.FavoriteResponse;
import com.example.javabackenddemo.dto.response.PageResponse;
import com.example.javabackenddemo.security.SecurityUtils;
import com.example.javabackenddemo.service.FavoriteService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/favorites")
public class FavoriteController {

    private final FavoriteService favoriteService;

    public FavoriteController(FavoriteService favoriteService) {
        this.favoriteService = favoriteService;
    }

    @PostMapping("/{productId}")
    public ApiResponse<FavoriteResponse> add(@PathVariable Long productId) {
        return ApiResponse.success(favoriteService.addFavorite(SecurityUtils.getCurrentUserId(), productId));
    }

    @DeleteMapping("/{productId}")
    public ApiResponse<Void> remove(@PathVariable Long productId) {
        favoriteService.removeFavorite(SecurityUtils.getCurrentUserId(), productId);
        return ApiResponse.success(null);
    }

    @GetMapping
    public ApiResponse<PageResponse<FavoriteResponse>> list(
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size) {
        return ApiResponse.success(PageResponse.from(favoriteService.listFavorites(SecurityUtils.getCurrentUserId(), page, size)));
    }

    @GetMapping("/{productId}/check")
    public ApiResponse<Map<String, Boolean>> check(@PathVariable Long productId) {
        return ApiResponse.success(Map.of("isFavorite", favoriteService.isFavorite(SecurityUtils.getCurrentUserId(), productId)));
    }
}
