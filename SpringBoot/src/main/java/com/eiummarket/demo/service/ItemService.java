package com.eiummarket.demo.service;

import com.eiummarket.demo.dto.ItemDto;
import com.eiummarket.demo.model.Item;
import com.eiummarket.demo.model.ItemImage;
import com.eiummarket.demo.model.Shop;
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

import static com.eiummarket.demo.model.Item.*;

@Service
@RequiredArgsConstructor
@Transactional
public class ItemService {


    private final ItemRepository itemRepository;
    private final ShopRepository shopRepository;
    private final FileStorageService fileStorageService;

    public ItemDto.Response createItem(ItemDto.CreateRequest request) {
        Shop shop = shopRepository.findById(request.getShopId())
                .orElseThrow(() -> new EntityNotFoundException("해당 상점을 찾을 수 없습니다."));

        Item item = builder()
                .shop(shop)
                .name(request.getName())
                .price(request.getPrice())
                .category(request.getCategory())
                .description(request.getDescription())
                .build();

        Item saved = itemRepository.save(item);
        if (request.getImageFiles() != null) {
            for (MultipartFile f : request.getImageFiles()) {
                String url = fileStorageService.storeFile(f);
                if (url != null) saved.getImages().add(ItemImage.builder().item(saved).url(url).build());
            }
        }
        if (request.getImageUrls() != null) {
            for (String url : request.getImageUrls()) {
                saved.getImages().add(ItemImage.builder().item(saved).url(url).build());
            }
        }

        return toResponse(saved);
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
                .createdAt(item.getCreatedAt())
                .imageUrls(item.getImages().stream().map(ItemImage::getUrl).toList())
                .build();
    }
}
