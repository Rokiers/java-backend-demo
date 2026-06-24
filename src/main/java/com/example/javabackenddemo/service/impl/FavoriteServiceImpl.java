package com.example.javabackenddemo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.javabackenddemo.dto.response.FavoriteResponse;
import com.example.javabackenddemo.entity.Favorite;
import com.example.javabackenddemo.entity.Product;
import com.example.javabackenddemo.entity.Sku;
import com.example.javabackenddemo.exception.DuplicateResourceException;
import com.example.javabackenddemo.exception.ResourceNotFoundException;
import com.example.javabackenddemo.mapper.FavoriteMapper;
import com.example.javabackenddemo.mapper.ProductMapper;
import com.example.javabackenddemo.mapper.SkuMapper;
import com.example.javabackenddemo.service.FavoriteService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class FavoriteServiceImpl implements FavoriteService {

    private final FavoriteMapper favoriteMapper;
    private final ProductMapper productMapper;
    private final SkuMapper skuMapper;

    public FavoriteServiceImpl(FavoriteMapper favoriteMapper, ProductMapper productMapper,
                               SkuMapper skuMapper) {
        this.favoriteMapper = favoriteMapper;
        this.productMapper = productMapper;
        this.skuMapper = skuMapper;
    }

    @Override
    public FavoriteResponse addFavorite(Long userId, Long productId) {
        if (productMapper.selectById(productId) == null)
            throw new ResourceNotFoundException("Product not found: " + productId);
        if (favoriteMapper.selectCount(new LambdaQueryWrapper<Favorite>()
                .eq(Favorite::getUserId, userId).eq(Favorite::getProductId, productId)) > 0) {
            throw new DuplicateResourceException("Product already in favorites");
        }
        Favorite fav = Favorite.builder().userId(userId).productId(productId).build();
        favoriteMapper.insert(fav);
        return toResponse(fav);
    }

    @Override
    public void removeFavorite(Long userId, Long productId) {
        Favorite fav = favoriteMapper.selectOne(new LambdaQueryWrapper<Favorite>()
                .eq(Favorite::getUserId, userId).eq(Favorite::getProductId, productId));
        if (fav == null) throw new ResourceNotFoundException("Favorite not found");
        favoriteMapper.deleteById(fav.getId());
    }

    @Override
    public Page<FavoriteResponse> listFavorites(Long userId, int page, int size) {
        Page<Favorite> result = favoriteMapper.selectPage(new Page<>(page + 1, size),
                new LambdaQueryWrapper<Favorite>().eq(Favorite::getUserId, userId).orderByDesc(Favorite::getCreatedAt));
        return convertPage(result, this::toResponse);
    }

    @Override
    public boolean isFavorite(Long userId, Long productId) {
        return favoriteMapper.selectCount(new LambdaQueryWrapper<Favorite>()
                .eq(Favorite::getUserId, userId).eq(Favorite::getProductId, productId)) > 0;
    }

    private FavoriteResponse toResponse(Favorite fav) {
        Product product = productMapper.selectById(fav.getProductId());
        String name = product != null ? product.getName() : "";
        String image = product != null ? product.getMainImage() : "";
        BigDecimal price = BigDecimal.ZERO;
        if (product != null) {
            List<Sku> skus = skuMapper.selectList(new LambdaQueryWrapper<Sku>().eq(Sku::getProductId, product.getId()));
            if (!skus.isEmpty()) price = skus.get(0).getPrice();
        }
        return new FavoriteResponse(fav.getId(), fav.getProductId(), name, image, price, fav.getCreatedAt());
    }

    @SuppressWarnings("unchecked")
    private <T, R> Page<R> convertPage(Page<T> source, java.util.function.Function<T, R> mapper) {
        Page<R> result = new Page<>(source.getCurrent(), source.getSize(), source.getTotal());
        result.setRecords(source.getRecords().stream().map(mapper).toList());
        return result;
    }
}
