package com.eiummarket.demo.service;

import com.eiummarket.demo.dto.ImageDto;
import com.eiummarket.demo.model.*;
import com.eiummarket.demo.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ImageService {

    private final MarketRepository marketRepository;
    private final ShopRepository shopRepository;
    private final ItemRepository itemRepository;
    private final ReviewRepository reviewRepository;
    private final MarketImageRepository marketImageRepository;
    private final ShopImageRepository shopImageRepository;
    private final ItemImageRepository itemImageRepository;
    private final ReviewImageRepository reviewImageRepository;

    private final String uploadDir = "uploads/";

    public ImageDto.Response uploadImage(Long marketId, Long shopId, Long itemId, Long reviewId, MultipartFile file, ImageDto.UploadRequest request) throws IOException {
        String imageUrl;

        // case 1: url 직접 입력
        if (request != null && request.getImageUrl() != null) {
            imageUrl = request.getImageUrl();
        }
        // case 2: 파일 업로드
        else if (file != null && !file.isEmpty()) {
            File dest = new File(uploadDir + file.getOriginalFilename());
            file.transferTo(dest);
            imageUrl = "/uploads/" + file.getOriginalFilename();
        } else {
            throw new IllegalArgumentException("파일 또는 URL 중 하나는 반드시 입력해야 합니다.");
        }

        // 저장 대상 결정
        if (reviewId != null) {
            Review review = reviewRepository.findById(reviewId).orElseThrow();
            ReviewImage image = reviewImageRepository.save(ReviewImage.builder().review(review).url(imageUrl).build());
            return new ImageDto.Response(image.getReviewImageId(), image.getUrl());
        } else if (itemId != null) {
            Item item = itemRepository.findById(itemId).orElseThrow();
            ItemImage image = itemImageRepository.save(ItemImage.builder().item(item).url(imageUrl).build());
            return new ImageDto.Response(image.getItemImageId(), image.getUrl());
        } else if (shopId != null) {
            Shop shop = shopRepository.findById(shopId).orElseThrow();
            ShopImage image = shopImageRepository.save(ShopImage.builder().shop(shop).url(imageUrl).build());
            return new ImageDto.Response(image.getShopImageId(), image.getUrl());
        } else {
            Market market = marketRepository.findById(marketId).orElseThrow();
            MarketImage image = marketImageRepository.save(MarketImage.builder().market(market).url(imageUrl).build());
            return new ImageDto.Response(image.getMarketImageId(), image.getUrl());
        }
    }

    public List<ImageDto.Response> getImages(Long marketId, Long shopId, Long itemId, Long reviewId) {
        if (reviewId != null) {
            return reviewImageRepository.findByReview_ReviewId(reviewId)
                    .stream().map(img -> new ImageDto.Response(img.getReviewImageId(), img.getUrl())).collect(Collectors.toList());
        } else if (itemId != null) {
            return itemImageRepository.findByItem_ItemId(itemId)
                    .stream().map(img -> new ImageDto.Response(img.getItemImageId(), img.getUrl())).collect(Collectors.toList());
        } else if (shopId != null) {
            return shopImageRepository.findByShop_ShopId(shopId)
                    .stream().map(img -> new ImageDto.Response(img.getShopImageId(), img.getUrl())).collect(Collectors.toList());
        } else if (marketId != null) {
            return marketImageRepository.findByMarket_MarketId(marketId)
                    .stream().map(img -> new ImageDto.Response(img.getMarketImageId(), img.getUrl())).collect(Collectors.toList());
        }
        return List.of();
    }

    public void deleteImage(Long marketId, Long shopId, Long itemId,Long imageId, Long reviewId) {
        if (reviewImageRepository.existsById(reviewId)&&reviewId!=null) {
            reviewImageRepository.deleteById(reviewId);
        }else if (itemImageRepository.existsById(imageId)&&itemId!=null) {
            itemImageRepository.deleteById(imageId);
        } else if (shopImageRepository.existsById(imageId)&&shopId!=null) {
            shopImageRepository.deleteById(imageId);
        } else if (marketImageRepository.existsById(imageId)&&marketId!=null) {
            marketImageRepository.deleteById(imageId);
        } else {
            throw new IllegalArgumentException("이미지를 찾을 수 없습니다.");
        }
    }
}
