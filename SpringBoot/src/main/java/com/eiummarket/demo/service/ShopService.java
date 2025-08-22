package com.eiummarket.demo.service;

import com.eiummarket.demo.Utils.SearchUtils;
import com.eiummarket.demo.dto.CategoryDto;
import com.eiummarket.demo.dto.ItemDto;
import com.eiummarket.demo.dto.ShopDto;
import com.eiummarket.demo.model.*;
import com.eiummarket.demo.repository.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.*;
import java.util.stream.Collectors;

import static com.eiummarket.demo.Utils.SearchUtils.addAll;
import static com.eiummarket.demo.Utils.SearchUtils.sortByPageable;
import static java.util.Collections.list;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ShopService {

    private final ShopRepository shopRepository;
    private final MarketRepository marketRepository;
    private final CategoryRepository categoryRepository;
    private final ItemRepository itemRepository;
    private final FileStorageService fileStorageService;


    /**
     * 상점 생성
     */
    @Transactional
    public ShopDto.Response createShop(Long marketId, ShopDto.CreateRequest request) {
        Market market = marketRepository.findById(marketId)
                .orElseThrow(() -> new EntityNotFoundException("해당 시장을 찾을 수 없습니다. ID=" + marketId));

        List<Category> categories = new ArrayList<>();
        if (request.getCategoryIds() != null && !request.getCategoryIds().isEmpty()) {
            categories = categoryRepository.findAllById(request.getCategoryIds());
            if (categories.size() != request.getCategoryIds().size()) {
                throw new IllegalArgumentException("일부 카테고리가 존재하지 않습니다.");
            }
        }

        Shop shop = Shop.builder()
                .market(market)
                .name(request.getName())
                .categories(categories)
                .phoneNumber(request.getPhoneNumber())
                .openingHours(request.getOpeningHours())
                .floor(request.getFloor())
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .description(request.getDescription())
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
    public Page<ShopDto.Response> getShops(Long marketId, String categoryName, Pageable pageable) {
        Category category = null;
        if (categoryName != null) {
            category = categoryRepository.findByName(categoryName)
                    .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 카테고리입니다: " + categoryName));
        }
        Page<Shop> page = (category == null)
                ? shopRepository.findAllByMarket_MarketId(marketId, pageable)
                : shopRepository.findAllByMarket_MarketIdAndCategoriesContaining(marketId, category, pageable);
        return page.map(this::toResponse);
    }


    /**
     * 상점 수정
     */

    @Transactional
    public ShopDto.Response updateShop(Long marketId, Long shopId, ShopDto.UpdateRequest request) {
        Shop shop = shopRepository.findByShopIdAndMarket_MarketId(shopId, marketId)
                .orElseThrow(() -> new IllegalArgumentException("해당 마켓에 속한 상점이 없습니다. marketId=" + marketId + ", shopId=" + shopId));

        Optional.ofNullable(request.getName()).ifPresent(shop::setName);
        Optional.ofNullable(request.getDescription()).ifPresent(shop::setDescription);

        // 이미지 개별 삭제
        if (request.getImageIds() != null) {
            shop.getImages().removeIf(img -> request.getImageIds().contains(img.getShopImageId()));
        }
        // 이미지 파일 개별 추가
        if (request.getImageFiles() != null) {
            for (MultipartFile f : request.getImageFiles()) {
                String url = fileStorageService.storeFile(f);
                if (url != null) shop.getImages().add(ShopImage.builder().shop(shop).url(url).build());
            }
        }
        // 이미지 URL 개별 추가
        if (request.getImageUrls() != null) {
            for (String url : request.getImageUrls()) {
                boolean exists = shop.getImages().stream().anyMatch(img -> img.getUrl().equals(url));
                if (!exists) shop.getImages().add(ShopImage.builder().shop(shop).url(url).build());
            }
        }

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

    public Page<ShopDto.Response> search(Long marketId, String keyword, Pageable pageable) {
        String sanitized = SearchUtils.sanitize(keyword);
        if (sanitized == null) {
            return shopRepository.findAllByMarket_MarketId(marketId, pageable).map(this::toResponse);
        }

        int cap = Math.max(pageable.getPageSize() * 5, 100);
        Pageable probe = PageRequest.of(0, cap);

        // marketId로 먼저 필터링 후 키워드 검색
        List<Shop> byName = shopRepository.findByMarket_MarketIdAndNameContaining(marketId, sanitized, probe).getContent();
        List<Shop> byDesc = shopRepository.findByMarket_MarketIdAndDescriptionContaining(marketId, sanitized, probe).getContent();
        List<Shop> byCategory = categoryRepository.findShopsByMarketIdAndCategoryNameLike(marketId, sanitized, probe).getContent();
        List<Shop> byItem = itemRepository.findShopsByMarketIdAndItemKeyword(marketId, sanitized, probe).getContent();

        Map<Long, Shop> merged = new LinkedHashMap<>();
        addAll(merged, byName);
        addAll(merged, byDesc);
        addAll(merged, byCategory);
        addAll(merged, byItem);

        List<Shop> all = new ArrayList<>(merged.values());
        sortByPageable(all, pageable);

        int total = all.size();
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), total);
        List<Shop> slice = (start >= total) ? List.of() : all.subList(start, end);

        return new PageImpl<>(slice.stream().map(this::toResponse).toList(), pageable, total);
    }


//    /** FavoriteService에서 재사용할 수 있도록 별도 노출 */
//    public ShopDto.Response toResponseForFavorite(Shop shop) {
//        return toResponse(shop);
//    }

    /** 엔티티→DTO 변환 */
    private ShopDto.Response toResponse(Shop shop) {
        var itemDtos = shop.getItems().stream().map(item ->
                ItemDto.Response.builder()
                        .itemId(item.getItemId()).shopId(shop.getShopId())
                        .name(item.getName()).price(item.getPrice())
                        .category(item.getCategory()).description(item.getDescription())
                        .createdAt(item.getCreatedAt())
                        .imageUrls(item.getImages().stream().map(ItemImage::getUrl).toList())
                        .build()
        ).toList();

        var categoryDtos = shop.getCategories().stream().map(cat ->
                CategoryDto.Response.builder().categoryId(cat.getCategoryId()).name(cat.getName()).build()
        ).toList();

        return ShopDto.Response.builder()
                .shopId(shop.getShopId()).marketId(shop.getMarket().getMarketId()).name(shop.getName())
                .address(shop.getAddress()).phoneNumber(shop.getPhoneNumber()).openingHours(shop.getOpeningHours())
                .floor(shop.getFloor()).latitude(shop.getLatitude()).longitude(shop.getLongitude())
                .description(shop.getDescription()).createdAt(shop.getCreatedAt())
                .favoriteCount(shop.getFavoriteCount())
                .items(itemDtos).categories(categoryDtos)
                .imageUrls(shop.getImages().stream().map(ShopImage::getUrl).toList())
                .build();
    }

    }
