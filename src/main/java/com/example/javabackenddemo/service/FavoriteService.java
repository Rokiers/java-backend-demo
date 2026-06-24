package com.example.javabackenddemo.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.javabackenddemo.dto.response.FavoriteResponse;

public interface FavoriteService {
    FavoriteResponse addFavorite(Long userId, Long productId);
    void removeFavorite(Long userId, Long productId);
    Page<FavoriteResponse> listFavorites(Long userId, int page, int size);
    boolean isFavorite(Long userId, Long productId);
}
