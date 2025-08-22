package com.eiummarket.demo.controller;

import com.eiummarket.demo.dto.ItemDto;
import com.eiummarket.demo.dto.ShopDto;
import com.eiummarket.demo.service.ItemService;
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

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "Item API", description = "상품 관련 API")
public class ItemController {

    private final ItemService itemService;

    @PostMapping(value="/markets/{marketId}/shops/{shopId}/items", consumes = {"multipart/form-data"})
    @Operation(summary = "상품 생성", description = "새로운 상품을 등록합니다.")
    public ResponseEntity<ItemDto.Response> createItem(@RequestBody ItemDto.CreateRequest request) {
        return ResponseEntity.ok(itemService.createItem(request));
    }

    @GetMapping("/markets/{marketId}/shops/{shopId}/items/{itemId}")
    @Operation(summary = "상품 상세 조회", description = "상품 ID로 상품 정보를 조회합니다.")
    public ResponseEntity<ItemDto.Response> getItem(@PathVariable Long itemId) {
        return ResponseEntity.ok(itemService.getItem(itemId));
    }
    @GetMapping("/items")
    @Operation(summary = "상점 내 상품 불러오기", description = "상점에서 취급하는 상품을 페이지네이션으로 조회합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공",
            content = @Content(schema = @Schema(implementation = ItemDto.Response.class)))
    public ResponseEntity<Page<ItemDto.Response>> listByShop(@RequestParam Long marketId,
                                                             @RequestParam Long shopId,
                                                             Pageable pageable) {
        return ResponseEntity.ok(itemService.listByShop(marketId, shopId, pageable));
    }

    @PutMapping(value="/markets/{marketId}/shops/{shopId}/items/{itemId}", consumes = {"multipart/form-data"})
    @Operation(summary = "상품 수정", description = "상품 ID로 상품 정보를 수정합니다.")
    public ResponseEntity<ItemDto.Response> updateItem(
            @PathVariable Long itemId,
            @RequestBody ItemDto.UpdateRequest request
    ) {
        return ResponseEntity.ok(itemService.updateItem(itemId, request));
    }

    @DeleteMapping("/markets/{marketId}/shops/{shopId}/items/{itemId}")
    @Operation(summary = "상품 삭제", description = "상품 ID로 상품을 삭제합니다.")
    public ResponseEntity<Void> deleteItem(@PathVariable Long itemId) {
        itemService.deleteItem(itemId);
        return ResponseEntity.noContent().build();
    }


}
