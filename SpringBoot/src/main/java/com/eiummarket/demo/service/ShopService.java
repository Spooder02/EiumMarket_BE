package com.eiummarket.demo.service;

import com.eiummarket.demo.dto.ShopDto;
import com.eiummarket.demo.model.Market;
import com.eiummarket.demo.model.Shop;
import com.eiummarket.demo.repository.MarketRepository;
import com.eiummarket.demo.repository.ShopRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ShopService {

    private final ShopRepository shopRepository;
    private final MarketRepository marketRepository;

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
                .build();

        Shop saved = shopRepository.save(shop);
        return toResponse(saved);
    }

    /**
     * 시장 내 상점 단건 조회
     */
    public ShopDto.Response getShop(Long marketId, Long shopId) {
        Shop shop = shopRepository.findByShopIdAndMarket_MarketId(shopId, marketId)
                .orElseThrow(() -> new EntityNotFoundException("상점을 찾을 수 없습니다. ID=" + shopId + ", MarketID=" + marketId));
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

        return toResponse(shop);
    }

    /**
     * 상점 삭제
     */
    @Transactional
    public void deleteShop(Long marketId, Long shopId) {
        Shop shop = shopRepository.findByIdAndMarketId(shopId, marketId)
                .orElseThrow(() -> new EntityNotFoundException("상점을 찾을 수 없습니다. ID=" + shopId + ", MarketID=" + marketId));

        shopRepository.delete(shop);
    }

    /**
     * 엔티티 → DTO 변환
     */
    private ShopDto.Response toResponse(Shop shop) {
        return ShopDto.Response.builder()
                .shopId(shop.getShopId())
                .marketId(shop.getMarket().getMarketId())
                .name(shop.getName())
                .category(shop.getCategory())
                .phoneNumber(shop.getPhoneNumber())
                .openingHours(shop.getOpeningHours())
                .floor(shop.getFloor())
                .latitude(shop.getLatitude())
                .longitude(shop.getLongitude())
                .description(shop.getDescription())
                .createdAt(shop.getCreatedAt())
                .build();
    }
}