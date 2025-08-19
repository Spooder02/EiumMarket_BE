package com.eiummarket.demo.service;

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
                .map(s -> ShopDto.Response.builder()
                        .shopId(s.getShopId())
                        .marketId(s.getMarket().getMarketId())
                        .name(s.getName())
                        .category(s.getCategory())
                        .phoneNumber(s.getPhoneNumber())
                        .openingHours(s.getOpeningHours())
                        .floor(s.getFloor())
                        .latitude(s.getLatitude())
                        .longitude(s.getLongitude())
                        .description(s.getDescription())
                        .createdAt(s.getCreatedAt())
                        .build());
    }
}
