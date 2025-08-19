package com.eiummarket.demo.service;

import com.eiummarket.demo.dto.ItemDto;
import com.eiummarket.demo.model.Item;
import com.eiummarket.demo.model.Market;
import com.eiummarket.demo.model.Shop;
import com.eiummarket.demo.repository.ItemRepository;
import com.eiummarket.demo.repository.ShopRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.eiummarket.demo.model.Item.*;

@Service
@RequiredArgsConstructor
@Transactional
public class ItemService {

    private final ItemRepository itemRepository;
    private final ShopRepository shopRepository;

    public ItemDto.Response createItem(ItemDto.CreateRequest request) {
        Shop shop = shopRepository.findById(request.getShopId())
                .orElseThrow(() -> new EntityNotFoundException("해당 상점을 찾을 수 없습니다."));

        Item item = builder()
                .shop(shop)
                .name(request.getName())
                .price(request.getPrice())
                .category(request.getCategory())
                .description(request.getDescription())
                .itemImageUrl(request.getItemImageUrl())
                .build();

        Item savedItem = itemRepository.save(item);
        return toResponse(savedItem);
    }

    @Transactional(readOnly = true)
    public ItemDto.Response getItem(Long itemId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new EntityNotFoundException("해당 상품을 찾을 수 없습니다."));
        return toResponse(item);
    }

    public ItemDto.Response updateItem(Long itemId, ItemDto.UpdateRequest request) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new EntityNotFoundException("해당 상품을 찾을 수 없습니다."));

        if (request.getName() != null) item.setName(request.getName());
        if (request.getPrice() != null) item.setPrice(request.getPrice());
        if (request.getCategory() != null) item.setCategory(request.getCategory());
        if (request.getDescription() != null) item.setDescription(request.getDescription());
        if (request.getItemImageUrl() != null) item.setItemImageUrl(request.getItemImageUrl());


        return toResponse(item);
    }

    public void deleteItem(Long itemId) {
        if (!itemRepository.existsById(itemId)) {
            throw new EntityNotFoundException("해당 상품을 찾을 수 없습니다.");
        }
        itemRepository.deleteById(itemId);
    }
    public Page<ItemDto.Response> listByShop(Long marketId, Long shopId, Pageable pageable) {
        Shop shop = shopRepository.findByShopIdAndMarket_MarketId(shopId, marketId)
                .orElseThrow(() -> new EntityNotFoundException("상점을 찾을 수 없습니다. ID=" + shopId + ", MarketID=" + marketId));

        return itemRepository.findAllByShop_ShopId(shop.getShopId(), pageable)
                .map(this::toResponse);
    }
    private ItemDto.Response toResponse(Item item) {
        return ItemDto.Response.builder()
                .itemId(item.getItemId())
                .shopId(item.getShop().getShopId())
                .name(item.getName())
                .price(item.getPrice())
                .category(item.getCategory())
                .description(item.getDescription())
                .itemImageUrl(item.getItemImageUrl())
                .createdAt(item.getCreatedAt())
                .build();
    }
}
