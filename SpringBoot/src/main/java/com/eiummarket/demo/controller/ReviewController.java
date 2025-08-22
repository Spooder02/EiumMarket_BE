// src/main/java/com/eiummarket/demo/controller/ReviewController.java
package com.eiummarket.demo.controller;

import com.eiummarket.demo.dto.ReviewDto;
import com.eiummarket.demo.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController @RequiredArgsConstructor
@Tag(name = "Review API", description = "리뷰 CRUD 및 조회")
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping("/reviews")
    @Operation(summary = "리뷰 생성", description = "Shop 또는 Item 중 하나에 대한 리뷰를 생성합니다. (이미지 URL/파일 포함 가능)")
    public ResponseEntity<ReviewDto.Response> create(@Valid @RequestBody ReviewDto.CreateRequest req) {
        return ResponseEntity.status(201).body(reviewService.createReview(req));
    }

    @GetMapping("/reviews/{reviewId}")
    @Operation(summary = "리뷰 단건 조회", description = "리뷰 ID로 조회합니다.")
    public ResponseEntity<ReviewDto.Response> get(@PathVariable Long reviewId) {
        return ResponseEntity.ok(reviewService.get(reviewId));
    }

    @GetMapping("/shops/{shopId}/reviews")
    @Operation(summary = "상점 리뷰 목록", description = "상점에 대한 리뷰를 페이지네이션으로 조회합니다.")
    public ResponseEntity<Page<ReviewDto.Response>> listByShop(@PathVariable Long shopId, Pageable pageable) {
        return ResponseEntity.ok(reviewService.listByShop(shopId, pageable));
    }

    @GetMapping("/items/{itemId}/reviews")
    @Operation(summary = "상품 리뷰 목록", description = "상품에 대한 리뷰를 페이지네이션으로 조회합니다.")
    public ResponseEntity<Page<ReviewDto.Response>> listByItem(@PathVariable Long itemId, Pageable pageable) {
        return ResponseEntity.ok(reviewService.listByItem(itemId, pageable));
    }

    @PutMapping("/reviews/{reviewId}")
    @Operation(summary = "리뷰 수정", description = "리뷰의 평점/내용을 수정합니다.")
    public ResponseEntity<ReviewDto.Response> update(@PathVariable Long reviewId,
                                                     @Valid @RequestBody ReviewDto.UpdateRequest req) {
        return ResponseEntity.ok(reviewService.updateReview(reviewId, req));
    }

    @DeleteMapping("/reviews/{reviewId}")
    @Operation(summary = "리뷰 삭제", description = "리뷰를 삭제합니다.")
    public ResponseEntity<Void> delete(@PathVariable Long reviewId) {
        reviewService.deleteReview(reviewId);
        return ResponseEntity.noContent().build();
    }
}
