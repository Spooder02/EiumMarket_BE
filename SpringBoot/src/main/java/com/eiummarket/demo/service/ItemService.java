package com.eiummarket.demo.service;

import com.eiummarket.demo.dto.ItemDto;
import com.eiummarket.demo.model.*;
import com.eiummarket.demo.repository.ItemImageRepository;
import com.eiummarket.demo.repository.ItemRepository;
import com.eiummarket.demo.repository.ShopRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import static com.eiummarket.demo.model.Item.*;

@Service
@RequiredArgsConstructor
@Transactional
public class ItemService {

    private final ItemRepository itemRepository;
    private final ShopRepository shopRepository;
    private final ItemImageRepository itemImageRepository;
    private final FileStorageService fileStorageService;

    // CREATE
    public ItemDto.Response createItem(Long marketId, Long shopId, ItemDto.CreateRequest req) {
        Shop shop = shopRepository.findByShopIdAndMarket_MarketId(shopId, marketId)
                .orElseThrow(() -> new EntityNotFoundException("해당 상점을 찾을 수 없습니다."));

        Item item = Item.builder()
                .shop(shop)
                .name(req.getName())
                .price(req.getPrice())
                .category(req.getCategory())
                .description(req.getDescription())
                .build();

        if (req.getImageFiles() != null) {
            System.out.println("이미지 파일 찾음");
            for (MultipartFile f : req.getImageFiles()) {
                String url = fileStorageService.storeFile(f);
                item.getImages().add(ItemImage.builder()
                        .item(item)
                        .url(url)
                        .build());
            }
        }
        if (req.getImageUrls() != null) {
            System.out.println("URL 찾음");
            for (String url : req.getImageUrls()) {
                item.getImages().add(ItemImage.builder()
                        .item(item)
                        .url(url)
                        .build());
            }
        }
        Item savedItem = itemRepository.save(item);

        return toResponse(savedItem);
    }

    @Transactional(readOnly = true)
    public ItemDto.Response getItem(Long marketId, Long shopId, Long itemId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new EntityNotFoundException("해당 상품을 찾을 수 없습니다."));

        if (!item.getShop().getShopId().equals(shopId) ||
                !item.getShop().getMarket().getMarketId().equals(marketId)) {
            throw new EntityNotFoundException("해당 상점에 속한 상품이 아닙니다.");
        }
        return toResponse(item);
    }

    // READ - 리스트
    @Transactional(readOnly = true)
    public Page<ItemDto.Response> listByShop(Long marketId, Long shopId, Pageable pageable) {
        Shop shop = shopRepository.findByShopIdAndMarket_MarketId(shopId, marketId)
                .orElseThrow(() -> new EntityNotFoundException("상점을 찾을 수 없습니다."));

        return itemRepository.findAllByShop_ShopId(shop.getShopId(), pageable)
                .map(this::toResponse);
    }

    public ItemDto.Response updateItem(Long itemId, ItemDto.UpdateRequest request) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new EntityNotFoundException("해당 상품을 찾을 수 없습니다."));

        if (request.getName() != null) item.setName(request.getName());
        if (request.getPrice() != null) item.setPrice(request.getPrice());
        if (request.getCategory() != null) item.setCategory(request.getCategory());
        if (request.getDescription() != null) item.setDescription(request.getDescription());

        // 이미지 삭제
        if (request.getImageIds() != null) {
            for (Long imageId : request.getImageIds()) {
                item.getImages().removeIf(img -> img.getItemImageId().equals(imageId));
            }
        }

        // 이미지 파일 추가
        if (request.getImageFiles() != null) {
            for (MultipartFile f : request.getImageFiles()) {
                String url = fileStorageService.storeFile(f);
                if (url != null) item.getImages().add(ItemImage.builder().item(item).url(url).build());
            }
        }
        // 이미지 URL 추가
        if (request.getImageUrls() != null) {
            for (String url : request.getImageUrls()) {
                boolean exists = item.getImages().stream().anyMatch(img -> img.getUrl().equals(url));
                if (!exists) item.getImages().add(ItemImage.builder().item(item).url(url).build());
            }
        }

        return toResponse(item);
    }

    public void deleteItem(Long marketId, Long shopId, Long itemId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new EntityNotFoundException("해당 상품을 찾을 수 없습니다."));

        if (!item.getShop().getShopId().equals(shopId) ||
                !item.getShop().getMarket().getMarketId().equals(marketId)) {
            throw new EntityNotFoundException("해당 상점에 속한 상품이 아닙니다.");
        }

        itemRepository.delete(item);
    }
    private Set<String> mergeAndCleanStrings(String existingStr, String requestStr) {
        Set<String> mergedSet = new HashSet<>();

        if (existingStr != null && !existingStr.trim().isEmpty()) {
            Arrays.stream(existingStr.split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .forEach(mergedSet::add);
        }

        if (requestStr != null && !requestStr.trim().isEmpty()) {
            Arrays.stream(requestStr.split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .forEach(mergedSet::add);
        }
        return mergedSet;
    }

    private ItemDto.Response toResponse(Item item) {
        return ItemDto.Response.builder()
                .itemId(item.getItemId())
                .shopId(item.getShop().getShopId())
                .name(item.getName())
                .price(item.getPrice())
                .category(item.getCategory())
                .description(item.getDescription())
                .createdAt(item.getCreatedAt())
                .imageUrls(item.getImages().stream().map(ItemImage::getUrl).toList())
                .build();
    }
}
