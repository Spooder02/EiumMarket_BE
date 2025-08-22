package com.eiummarket.demo.controller;

import com.eiummarket.demo.dto.ImageDto;
import com.eiummarket.demo.service.ImageService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/images")
@RequiredArgsConstructor
public class ImageController {

    private final ImageService imageService;

    @Operation(summary = "이미지 업로드 (market/shop/item)")
    @PostMapping
    public ResponseEntity<ImageDto.Response> uploadImage(
            @RequestParam Long marketId,
            @RequestParam(required = false) Long shopId,
            @RequestParam(required = false) Long itemId,
            @RequestParam(required = false) Long reviewId,
            @RequestPart(required = false) MultipartFile file,
            @RequestPart(required = false) ImageDto.UploadRequest request
    ) throws IOException {
        return ResponseEntity.ok(imageService.uploadImage(marketId, shopId, itemId, reviewId, file, request));
    }

    @Operation(summary = "특정 엔티티 이미지 조회")
    @GetMapping
    public ResponseEntity<List<ImageDto.Response>> getImages(
            @RequestParam Long marketId,
            @RequestParam(required = false) Long shopId,
            @RequestParam(required = false) Long itemId,
            @RequestParam(required = false) Long reviewId
    ) {
        return ResponseEntity.ok(imageService.getImages(marketId, shopId, itemId, reviewId));
    }

    @Operation(summary = "이미지 삭제")
    @DeleteMapping("/{imageId}")
    public ResponseEntity<Void> deleteImage(
            @RequestParam Long marketId,
            @RequestParam(required = false) Long shopId,
            @RequestParam(required = false) Long itemId,
            @RequestParam(required = false) Long reviewId,
            @PathVariable Long imageId
    ) {
        imageService.deleteImage(marketId, shopId, itemId, imageId, reviewId);
        return ResponseEntity.noContent().build();
    }
}
