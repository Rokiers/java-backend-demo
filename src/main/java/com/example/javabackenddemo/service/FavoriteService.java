package com.example.javabackenddemo.service;

import com.example.javabackenddemo.dto.response.FavoriteResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface FavoriteService {
    FavoriteResponse addFavorite(Long userId, Long productId);
    void removeFavorite(Long userId, Long productId);
    Page<FavoriteResponse> listFavorites(Long userId, Pageable pageable);
    boolean isFavorite(Long userId, Long productId);
}
