package com.eiummarket.demo.service;

import com.eiummarket.demo.Utils.SearchUtils;
import com.eiummarket.demo.dto.CategoryDto;
import com.eiummarket.demo.dto.ItemDto;
import com.eiummarket.demo.dto.ShopDto;
import com.eiummarket.demo.model.Item;
import com.eiummarket.demo.model.Market;
import com.eiummarket.demo.model.Shop;
import com.eiummarket.demo.repository.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.*;

import java.util.List;
import java.util.stream.Collectors;

import static java.util.Collections.list;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ShopService {

    private final ShopRepository shopRepository;
    private final MarketRepository marketRepository;
    private final ItemRepository itemRepository;

    /**
     * 상점 생성
     */
    @Transactional
    public ShopDto.Response createShop(Long marketId, ShopDto.CreateRequest request) {
        Market market = marketRepository.findById(marketId)
                .orElseThrow(() -> new EntityNotFoundException("해당 시장을 찾을 수 없습니다. ID=" + marketId));

        Shop shop = Shop.builder()
                .market(market)
                .name(request.getName())
                .category(request.getCategory())
                .phoneNumber(request.getPhoneNumber())
                .openingHours(request.getOpeningHours())
                .floor(request.getFloor())
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .description(request.getDescription())
                .shopImageUrl(request.getShopImageUrl())
                .address(request.getAddress())
                .favoriteCount(0L)
                .build();

        Shop saved = shopRepository.save(shop);
        return toResponse(saved);
    }

    /**
     * 시장 내 상점 단건 조회
     */
    public ShopDto.Response getShop(Long marketId, Long shopId) {
        Shop shop = shopRepository.findByShopIdAndMarket_MarketId(shopId, marketId)
                .orElseThrow(() -> new IllegalArgumentException("Shop not found"));
        return toResponse(shop);
    }


    /**
     * 시장 내 상점들 조회
     */
    public Page<ShopDto.Response> getShops(Long marketId, String category, Pageable pageable) {
        Page<Shop> page = (category == null || category.isBlank())
                ? shopRepository.findAllByMarket_MarketId(marketId, pageable)
                : shopRepository.findAllByMarket_MarketIdAndCategory(marketId, category, pageable);
        return page.map(this::toResponse);
    }

    /**
     * 상점 수정
     */

    @Transactional
    public ShopDto.Response updateShop(Long marketId, Long shopId, ShopDto.UpdateRequest request) {
        Shop shop = shopRepository.findByShopIdAndMarket_MarketId(shopId, marketId)
                .orElseThrow(() -> new EntityNotFoundException("상점을 찾을 수 없습니다. ID=" + shopId + ", MarketID=" + marketId));

        if (request.getName() != null) shop.setName(request.getName());
        if (request.getCategory() != null) shop.setCategory(request.getCategory());
        if (request.getPhoneNumber() != null) shop.setPhoneNumber(request.getPhoneNumber());
        if (request.getOpeningHours() != null) shop.setOpeningHours(request.getOpeningHours());
        if (request.getFloor() != null) shop.setFloor(request.getFloor());
        if (request.getLatitude() != null) shop.setLatitude(request.getLatitude());
        if (request.getLongitude() != null) shop.setLongitude(request.getLongitude());
        if (request.getDescription() != null) shop.setDescription(request.getDescription());
        if (request.getShopImageUrl() != null) shop.setShopImageUrl(request.getShopImageUrl());
        if (request.getAddress() != null) shop.setAddress(request.getAddress());

        return toResponse(shop);
    }

    /**
     * 상점 삭제
     */
    @Transactional
    public void deleteShop(Long marketId, Long shopId) {
        Shop shop = shopRepository.findByShopIdAndMarket_MarketId(shopId, marketId)
                .orElseThrow(() -> new EntityNotFoundException("상점을 찾을 수 없습니다. ID=" + shopId + ", MarketID=" + marketId));
        shopRepository.delete(shop);
    }

    public Page<ShopDto.Response> search(String keyword, Pageable pageable) {
        String sanitized= SearchUtils.sanitize(keyword);
        if (sanitized==null){
            return shopRepository.findAll(pageable).map(this::toResponse);
        }
        String escapedKeyword = SearchUtils.escapeLike(sanitized.toLowerCase());
        return shopRepository.searchByAllKeywords(escapedKeyword, pageable)
                .map(this::toResponse);
    }

    /**
     * 엔티티 → DTO 변환
     */
    private ShopDto.Response toResponse(Shop shop) {
        // Item 엔티티를 ItemDto.Response로 변환하는 로직
        List<ItemDto.Response> itemDtos = shop.getItems().stream()
                .map(item -> ItemDto.Response.builder()
                        .itemId(item.getItemId())
                        .shopId(item.getShop().getShopId())
                        .name(item.getName())
                        .price(item.getPrice())
                        .category(item.getCategory())
                        .description(item.getDescription())
                        .createdAt(item.getCreatedAt())
                        .build())
                .collect(Collectors.toList());

        return ShopDto.Response.builder()
                .shopId(shop.getShopId())
                .marketId(shop.getMarket().getMarketId())
                .name(shop.getName())
                .category(shop.getCategory())
                .shopImageUrl(shop.getShopImageUrl())
                .address(shop.getAddress())
                .phoneNumber(shop.getPhoneNumber())
                .openingHours(shop.getOpeningHours())
                .floor(shop.getFloor())
                .latitude(shop.getLatitude())
                .longitude(shop.getLongitude())
                .description(shop.getDescription())
                .createdAt(shop.getCreatedAt())
                .favoriteCount(shop.getFavoriteCount())
                .items(itemDtos)
                .build();
    }
}