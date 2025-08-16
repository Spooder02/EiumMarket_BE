package com.eiummarket.demo.controller;

import com.eiummarket.demo.dto.ShopDto;
import com.eiummarket.demo.service.FavoriteService;
import com.eiummarket.demo.service.ShopService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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

    // 시장 내 상점 목록 조회 (category 필터)
    @GetMapping
    @Operation(summary = "시장 내 상점 목록 조회",
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
    @PostMapping
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
    @PutMapping("/{shopId}")
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
}
