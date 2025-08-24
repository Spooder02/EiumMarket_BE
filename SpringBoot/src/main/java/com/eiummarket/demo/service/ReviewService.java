// src/main/java/com/eiummarket/demo/service/ReviewService.java
package com.eiummarket.demo.service;

import com.eiummarket.demo.dto.ReviewDto;
import com.eiummarket.demo.model.*;
import com.eiummarket.demo.repository.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service @RequiredArgsConstructor @Transactional(readOnly = true)
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ShopRepository shopRepository;
    private final ItemRepository itemRepository;
    private final ReviewImageRepository reviewImageRepository;
    private final FileStorageService fileStorageService;

    @Transactional
    public ReviewDto.Response createReview(ReviewDto.CreateRequest req) {
        if ((req.getShopId() == null) && (req.getItemId() == null)|| (req.getShopId() != null && req.getItemId() != null)) {
            throw new IllegalArgumentException("shopId 또는 itemId 중 하나만 지정해야 합니다.");
        }
        Shop shop = null;
        Item item = null;
        if (req.getShopId() != null) {
            shop = shopRepository.findById(req.getShopId())
                    .orElseThrow(() -> new EntityNotFoundException("상점 없음: " + req.getShopId()));
        } else {
            item = itemRepository.findById(req.getItemId())
                    .orElseThrow(() -> new EntityNotFoundException("상품 없음: " + req.getItemId()));
        }

        Review saved = reviewRepository.save(
                Review.builder()
                        .shop(shop).item(item).userId(req.getUserId())
                        .rating(req.getRating()).content(req.getContent())
                        .build()
        );

        if (req.getImageFiles() != null) {
            for (MultipartFile f : req.getImageFiles()) {
                String url = fileStorageService.storeFile(f);
                if (url != null) reviewImageRepository.save(ReviewImage.builder().review(saved).url(url).build());
            }
        }
        if (req.getImageUrls() != null) {
            for (String url : req.getImageUrls()) {
                reviewImageRepository.save(ReviewImage.builder().review(saved).url(url).build());
            }
        }
        return toResponse(saved);
    }

    public ReviewDto.Response get(Long reviewId) {
        Review r = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new EntityNotFoundException("리뷰 없음: " + reviewId));
        return toResponse(r);
    }

    public Page<ReviewDto.Response> listByShop(Long shopId, Pageable pageable) {
        return reviewRepository.findByShop_ShopId(shopId, pageable).map(this::toResponse);
    }

    public Page<ReviewDto.Response> listByItem(Long itemId, Pageable pageable) {
        return reviewRepository.findByItem_ItemId(itemId, pageable).map(this::toResponse);
    }

    @Transactional
    public ReviewDto.Response updateReview(Long reviewId, ReviewDto.UpdateRequest req) {
        Review r = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new EntityNotFoundException("리뷰 없음: " + reviewId));
        if (req.getRating() != null) r.setRating(req.getRating());
        if (req.getContent() != null) r.setContent(req.getContent());

        // 이미지 전체 삭제
        if (req.getImageIds() != null) {
            r.getImages().clear();
        }
        // 이미지 파일 개별 추가
        if (req.getImageFiles() != null) {
            for (MultipartFile f : req.getImageFiles()) {
                String url = fileStorageService.storeFile(f);
                if (url != null) r.getImages().add(ReviewImage.builder().review(r).url(url).build());
            }
        }
        // 이미지 URL 개별 추가
        if (req.getImageUrls() != null) {
            for (String url : req.getImageUrls()) {
                boolean exists = r.getImages().stream().anyMatch(img -> img.getUrl().equals(url));
                if (!exists) r.getImages().add(ReviewImage.builder().review(r).url(url).build());
            }
        }
        return toResponse(r);
    }

    @Transactional
    public void deleteReview(Long reviewId) {
        if (!reviewRepository.existsById(reviewId)) throw new EntityNotFoundException("리뷰 없음: " + reviewId);
        reviewRepository.deleteById(reviewId);
    }

    private ReviewDto.Response toResponse(Review r) {
        return ReviewDto.Response.builder()
                .reviewId(r.getReviewId()).userId(r.getUserId())
                .rating(r.getRating()).content(r.getContent())
                .shopId(r.getShop() == null ? null : r.getShop().getShopId())
                .itemId(r.getItem() == null ? null : r.getItem().getItemId())
                .createdAt(r.getCreatedAt())
                .imageUrls(r.getImages().stream().map(ReviewImage::getUrl).toList())
                .build();
    }
}
