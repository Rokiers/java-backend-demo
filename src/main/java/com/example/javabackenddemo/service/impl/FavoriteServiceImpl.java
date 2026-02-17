package com.example.javabackenddemo.service.impl;

import com.example.javabackenddemo.dto.response.FavoriteResponse;
import com.example.javabackenddemo.entity.Favorite;
import com.example.javabackenddemo.entity.Product;
import com.example.javabackenddemo.entity.Sku;
import com.example.javabackenddemo.exception.DuplicateResourceException;
import com.example.javabackenddemo.exception.ResourceNotFoundException;
import com.example.javabackenddemo.repository.FavoriteRepository;
import com.example.javabackenddemo.repository.ProductRepository;
import com.example.javabackenddemo.repository.SkuRepository;
import com.example.javabackenddemo.service.FavoriteService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class FavoriteServiceImpl implements FavoriteService {

    private final FavoriteRepository favoriteRepository;
    private final ProductRepository productRepository;
    private final SkuRepository skuRepository;

    public FavoriteServiceImpl(FavoriteRepository favoriteRepository, ProductRepository productRepository,
                               SkuRepository skuRepository) {
        this.favoriteRepository = favoriteRepository;
        this.productRepository = productRepository;
        this.skuRepository = skuRepository;
    }

    @Override
    public FavoriteResponse addFavorite(Long userId, Long productId) {
        productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + productId));
        if (favoriteRepository.existsByUserIdAndProductId(userId, productId)) {
            throw new DuplicateResourceException("Product already in favorites");
        }
        Favorite fav = Favorite.builder().userId(userId).productId(productId).build();
        return toResponse(favoriteRepository.save(fav));
    }

    @Override
    public void removeFavorite(Long userId, Long productId) {
        Favorite fav = favoriteRepository.findByUserIdAndProductId(userId, productId)
                .orElseThrow(() -> new ResourceNotFoundException("Favorite not found"));
        favoriteRepository.delete(fav);
    }

    @Override
    public Page<FavoriteResponse> listFavorites(Long userId, Pageable pageable) {
        return favoriteRepository.findByUserId(userId, pageable).map(this::toResponse);
    }

    @Override
    public boolean isFavorite(Long userId, Long productId) {
        return favoriteRepository.existsByUserIdAndProductId(userId, productId);
    }

    private FavoriteResponse toResponse(Favorite fav) {
        Product product = productRepository.findById(fav.getProductId()).orElse(null);
        String name = product != null ? product.getName() : "";
        String image = product != null ? product.getMainImage() : "";
        BigDecimal price = BigDecimal.ZERO;
        if (product != null) {
            List<Sku> skus = skuRepository.findByProductId(product.getId());
            if (!skus.isEmpty()) price = skus.get(0).getPrice();
        }
        return new FavoriteResponse(fav.getId(), fav.getProductId(), name, image, price, fav.getCreatedAt());
    }
}
