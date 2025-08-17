package com.eiummarket.demo.controller;

import com.eiummarket.demo.dto.ShopDto;
import com.eiummarket.demo.service.FavoriteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Tag(name = "Favorite API", description = "가게 찜(즐겨찾기) 관리 API")
public class FavoriteController {

    private final FavoriteService favoriteService;

    @PostMapping("/shops/{shopId}/favorites")
    @Operation(summary = "가게 찜하기", description = "특정 가게를 찜 처리합니다.")
    public ResponseEntity<Void> like(@PathVariable Long shopId,
                                     @RequestParam Integer userId) {
        favoriteService.likeShop(shopId, userId);
        return ResponseEntity.status(201).build();
    }

    @DeleteMapping("/shops/{shopId}/favorites")
    @Operation(summary = "가게 찜 취소", description = "특정 가게의 찜을 취소합니다.")
    public ResponseEntity<Void> unlike(@PathVariable Long shopId,
                                       @RequestParam Integer userId) {
        favoriteService.unlikeShop(shopId, userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/favorites")
    @Operation(summary = "찜한 가게 목록 조회", description = "사용자가 찜한 가게를 페이지 단위로 조회합니다. marketId를 주면 해당 시장으로 필터링합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공",
            content = @Content(schema = @Schema(implementation = ShopDto.Response.class)))
    public ResponseEntity<Page<ShopDto.Response>> list(@RequestParam Integer userId,
                                                       @RequestParam(required = false) Long marketId,
                                                       Pageable pageable) {
        return ResponseEntity.ok(favoriteService.listFavorites(userId, marketId, pageable));
    }
}
