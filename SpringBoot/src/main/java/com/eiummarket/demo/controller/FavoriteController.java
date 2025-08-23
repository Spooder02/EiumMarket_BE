package com.eiummarket.demo.controller;

import com.eiummarket.demo.dto.ShopDto;
import com.eiummarket.demo.service.FavoriteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Favorite", description = "찜 관련 API")
@RestController
@RequestMapping("/markets/{marketId}")
@RequiredArgsConstructor
public class FavoriteController {

    private final FavoriteService favoriteService;

    @Operation(summary = "상점 찜하기", description = "특정 시장의 상점을 찜 목록에 추가합니다.")
    @PostMapping("/shops/{shopId}/favorites")
    public ResponseEntity<Void> likeShop(
            @PathVariable Long marketId,
            @PathVariable Long shopId) {
        favoriteService.likeShop(marketId, shopId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "상점 찜 취소", description = "특정 시장의 상점을 찜 목록에서 제거합니다.")
    @DeleteMapping("/shops/{shopId}/favorites")
    public ResponseEntity<Void> unlikeShop(
            @PathVariable Long marketId,
            @PathVariable Long shopId) {
        favoriteService.unlikeShop(marketId, shopId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "찜 목록 조회", description = "특정 시장의 찜 목록을 조회합니다.")
    @GetMapping("/favorites")
    public ResponseEntity<Page<ShopDto.Response>> listFavorites(
            @PathVariable Long marketId,
            Pageable pageable) {
        return ResponseEntity.ok(favoriteService.listFavorites(marketId, pageable));
    }
}
