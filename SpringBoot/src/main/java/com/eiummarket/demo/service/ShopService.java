package com.eiummarket.demo.service;

import com.eiummarket.demo.dto.ItemDto;
import com.eiummarket.demo.dto.ShopDto;
import com.eiummarket.demo.model.Item;
import com.eiummarket.demo.model.Market;
import com.eiummarket.demo.model.Shop;
import com.eiummarket.demo.repository.ItemRepository;
import com.eiummarket.demo.repository.MarketRepository;
import com.eiummarket.demo.repository.ShopRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ShopService {

    private final ShopRepository shopRepository;
    private final MarketRepository marketRepository;
    private final ItemRepository itemRepository;

    private final WebClient webClient;

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

    public List<ShopDto.Response> search(String keyword) {
        Set<Shop> result = new HashSet<>();

        // 1. 가게명 검색
        result.addAll(shopRepository.findByNameContainingIgnoreCase(keyword));
        // 2. 카테고리 검색
        result.addAll(shopRepository.findByCategoryContainingIgnoreCase(keyword));
        // 3. 아이템 검색 (아이템명/설명)
        result.addAll(itemRepository.findByNameContainingIgnoreCaseOrDescriptionContaining(keyword, keyword)
                .stream()
                .map(Item::getShop)
                .collect(Collectors.toSet()));

        // 결과 변환
        return result.stream().map(shop -> {
            ShopDto.Response response = toResponse(shop);
            response.setMatchedKeywords(List.of(keyword));
            return response;
        }).collect(Collectors.toList());
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

    /**
     * AI 관련 API
     */

    public String getShopItemDescription(Long marketId, Long shopId, String shopName) {
        Mono<String> responseMono = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/text/description")
                        .queryParam("title", shopName)
                        .build())
                .retrieve()
                .bodyToMono(String.class);

        return responseMono.block();
    }
}