package com.eiummarket.demo.controller;

import com.eiummarket.demo.dto.ShopDto;
import com.eiummarket.demo.service.AiImageService;
import com.eiummarket.demo.service.MarketService;
import com.eiummarket.demo.service.ShopService;
import com.eiummarket.demo.service.AiImageService.AiImageDomain;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/markets/{marketId}/shops")
@RequiredArgsConstructor
@Tag(name = "Shop API", description = "가게 및 상품 관리 API")
public class ShopController {

    private final ShopService shopService;
    private final AiImageService aiImageService;
    private final MarketService marketService;

    @Operation(summary = "시장 내 상점 전체/카테고리별 목록 조회",
            description = "특정 시장에 속한 모든 혹은 특정 카테고리의 상점을 페이지네이션으로 조회합니다. (카테고리가 null이라면 모든 상점을 가져옵니다)")
    @ApiResponse(responseCode = "200", description = "조회 성공",
            content = @Content(schema = @Schema(implementation = ShopDto.Response.class)))
    public ResponseEntity<Page<ShopDto.Response>> listShops(
            @PathVariable Long marketId,
            @RequestParam(required = false) String category,
            Pageable pageable) {
        return ResponseEntity.ok(shopService.getShops(marketId, category, pageable));
    }
    // 시장 내 상점 등록
    @PostMapping(consumes = {"multipart/form-data"})
    @Operation(summary = "상점 등록", description = "시장 내 새로운 상점을 등록합니다.")
    @ApiResponse(responseCode = "201", description = "생성 성공",
            content = @Content(schema = @Schema(implementation = ShopDto.Response.class)))
    public ResponseEntity<ShopDto.Response> createShop(
            @PathVariable Long marketId,
            @Valid @RequestBody ShopDto.CreateRequest req) {
        return ResponseEntity.status(201).body(shopService.createShop(marketId, req));
    }
    // 시장 내 상점 조회
    @GetMapping("/{shopId}")
    @Operation(summary = "상점 조회", description = "시장 내 특정 상점 정보를 조회합니다.")
    public ResponseEntity<ShopDto.Response> getShop(
            @PathVariable Long marketId,
            @PathVariable Long shopId) {
        return ResponseEntity.ok(shopService.getShop(marketId, shopId));
    }
    // 가게 정보 수정
    @PatchMapping(value = "/{shopId}", consumes = {"multipart/form-data"})
    @Operation(summary = "가게 정보 수정", description = "상점의 전체 정보를 수정합니다.")
    public ResponseEntity<ShopDto.Response> updateShop(
            @PathVariable Long marketId,
            @PathVariable Long shopId,
            @Valid @RequestBody ShopDto.UpdateRequest req) {
        return ResponseEntity.ok(shopService.updateShop(marketId, shopId, req));
    }
    // 가게 삭제
    @DeleteMapping("/{shopId}")
    @Operation(summary = "가게 삭제", description = "상점을 삭제합니다.")
    public ResponseEntity<Void> deleteShop(
            @PathVariable Long marketId,
            @PathVariable Long shopId) {
        shopService.deleteShop(marketId, shopId);
        return ResponseEntity.noContent().build();
    }

    /** AI 관련 API */
    @GetMapping("/{shopId}/ai/description")
    @Operation(summary = "AI 상품 설명 생성", description = "가게 이름을 기반으로 AI가 상품 설명/카테고리를 생성하여 반환합니다.")
    public ResponseEntity<String> getShopItemDescription(@PathVariable Long marketId,
                                                        @PathVariable Long shopId,
                                                        @RequestParam String shopName) {

        String description = shopService.getShopItemDescription(marketId, shopId, shopName);
        return ResponseEntity.ok(description);
    }

    
    @GetMapping("/ai/image-generate")
    @Operation(summary = "AI 상품 이미지 생성 및 저장", description = "상품명을 기반으로 AI 이미지를 생성하고 서버에 저장한 뒤, 접근 가능한 URL을 반환합니다.")
    @ApiResponse(responseCode = "200", description = "이미지 저장 성공 및 URL 반환")
    public ResponseEntity<String> generateAiShopImage(
            @PathVariable Long marketId,
            @Parameter(description = "이미지를 생성할 상점명", required = true, example = "신선한 과일 가게")
            @RequestParam String shopName,
            @Parameter(description = "이미지를 생성할 상점의 소개", required = true, example = "신선한 과일을 매일 공급하는 가게")
            @RequestParam String description) {
        
        String marketName = marketService.getMarketNameById(marketId);

        String imageUrl = aiImageService.generateAndStoreImage(
            AiImageService.AiImageDomain.SHOP,
            null,       // itemName
            marketName,
            shopName,
            description 
    );


        return ResponseEntity.ok(imageUrl);
    }

    @GetMapping("/{shopId}/items/ai/image-generate")
    @Operation(summary = "AI 상품 이미지 생성 및 저장", description = "상품명을 기반으로 AI 이미지를 생성하고 서버에 저장한 뒤, 접근 가능한 URL을 반환합니다.")
    @ApiResponse(responseCode = "200", description = "이미지 저장 성공 및 URL 반환")
    public ResponseEntity<String> generateAiItemImage(
            @PathVariable Long marketId,
            @PathVariable Long shopId,
            @Parameter(description = "이미지를 생성할 상품명", required = true, example = "신선한 사과")
            @RequestParam String itemName) {

        String imageUrl = aiImageService.generateAndStoreImage(AiImageService.AiImageDomain.PRODUCT, itemName, null, null, null);

        return ResponseEntity.ok(imageUrl);
    }

    @GetMapping("/search")
    @Operation(summary = "시장 내 가게 검색", description = "시장에 파라미터를 포함한 물건, 카테고리를 판매중인 가게 혹은 가게명이 있는지 검색 후 페이지네이션해 반환합니다.")
    public ResponseEntity<Page<ShopDto.Response>> searchShopList(
            @PathVariable Long marketId,
            @Parameter(description = "검색어(대소문자 무시, 부분 일치", example = "상추")
            @RequestParam(value = "search") String search,
            @ParameterObject Pageable pageable
    ){
        return ResponseEntity.ok(shopService.search(marketId,search,pageable));
    }


}
