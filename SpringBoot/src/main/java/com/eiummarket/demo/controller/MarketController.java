package com.eiummarket.demo.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


import com.eiummarket.demo.dto.MarketDto;
import com.eiummarket.demo.service.MarketService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/markets")
@RequiredArgsConstructor
@Tag(name = "Market API", description = "시장 정보 CRUD API")
public class MarketController {

    private final MarketService marketService;

    @PostMapping(consumes = {"multipart/form-data"})
    @Operation(summary = "시장 등록하기", description = "시장 정보를 신규 등록합니다.")
    @ApiResponse(responseCode = "201", description = "생성 성공",
            content = @Content(schema = @Schema(implementation = MarketDto.Response.class)))
    public ResponseEntity<MarketDto.Response> create(@Valid @ModelAttribute MarketDto.CreateRequest req) {
        MarketDto.Response res = marketService.create(req);
        return ResponseEntity.status(201).body(res);
    }

    @GetMapping("/{marketId}")
    @Operation(summary = "시장 단일 조회", description = "ID로 시장 정보를 조회합니다.")
    public ResponseEntity<MarketDto.Response> get(@PathVariable Long marketId) {
        return ResponseEntity.ok(marketService.get(marketId));
    }

    @GetMapping
    @Operation(summary = "시장 목록 조회", description = "페이지 단위로 시장 목록을 조회합니다.")
    public ResponseEntity<Page<MarketDto.Response>> list(Pageable pageable) {
        return ResponseEntity.ok(marketService.list(pageable));
    }

    @PatchMapping(value = "/{marketId}", consumes = {"multipart/form-data"})
    @Operation(summary = "시장 부분 수정", description = "전달된 필드만 부분 업데이트합니다.")
    public ResponseEntity<MarketDto.Response> update(@PathVariable Long marketId,
                                                     @Valid @ModelAttribute MarketDto.UpdateRequest req) {
        return ResponseEntity.ok(marketService.update(marketId, req));
    }

    @DeleteMapping("/{marketId}")
    @Operation(summary = "시장 삭제", description = "해당 ID의 시장 정보를 삭제합니다.")
    public ResponseEntity<Void> delete(@PathVariable Long marketId) {
        marketService.delete(marketId);
        return ResponseEntity.noContent().build();
    }
}