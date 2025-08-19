package com.eiummarket.demo.service;

import com.eiummarket.demo.dto.CategoryDto;
import com.eiummarket.demo.dto.ShopDto;
import com.eiummarket.demo.model.Favorite;
import com.eiummarket.demo.model.Shop;
import com.eiummarket.demo.repository.FavoriteRepository;
import com.eiummarket.demo.repository.ShopRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FavoriteService {

    private final FavoriteRepository favoriteRepository;
    private final ShopRepository shopRepository;

    @Transactional
    public void likeShop(Long shopId, Integer userId) {
        if (favoriteRepository.existsByShop_ShopIdAndUserId(shopId, userId)) return;
        Shop shop = shopRepository.findById(shopId)
                .orElseThrow(() -> new EntityNotFoundException("상점을 찾을 수 없습니다. ID=" + shopId));
        favoriteRepository.save(Favorite.builder().shop(shop).userId(userId).build());
    }

    @Transactional
    public void unlikeShop(Long shopId, Integer userId) {
        favoriteRepository.deleteByShop_ShopIdAndUserId(shopId, userId);
    }

    public Page<ShopDto.Response> listFavorites(Integer userId, Long marketId, Pageable pageable) {
        return shopRepository.findFavoriteShopsByUserId(userId, marketId, pageable)
                .map(this::toResponse);
    }

    private ShopDto.Response toResponse(Shop shop) {
        return ShopDto.Response.builder()
                .shopId(shop.getShopId())
                .marketId(shop.getMarket().getMarketId())
                .name(shop.getName())
                .phoneNumber(shop.getPhoneNumber())
                .openingHours(shop.getOpeningHours())
                .floor(shop.getFloor())
                .latitude(shop.getLatitude())
                .longitude(shop.getLongitude())
                .description(shop.getDescription())
                .createdAt(shop.getCreatedAt())
                .categories(shop.getCategories().stream()
                        .map(cat -> CategoryDto.Response.builder()
                                .categoryId(cat.getCategoryId())
                                .name(cat.getName())
                                .build())
                        .collect(Collectors.toList()))
                .build();
    }
}