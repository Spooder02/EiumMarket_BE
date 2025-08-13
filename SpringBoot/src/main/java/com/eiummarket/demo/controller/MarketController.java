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
@RequestMapping("/api/markets")
@RequiredArgsConstructor
@Tag(name = "Market API", description = "시장 정보 CRUD API")
public class MarketController {

    private final com.eiummarket.demo.service.MarketService marketService;

    @PostMapping
    @Operation(summary = "시장 생성", description = "시장 정보를 신규 등록합니다.")
    @ApiResponse(responseCode = "201", description = "생성 성공",
            content = @Content(schema = @Schema(implementation = MarketDto.Response.class)))
    public ResponseEntity<MarketDto.Response> create(@Valid @RequestBody MarketDto.CreateRequest req) {
        MarketDto.Response res = marketService.create(req);
        return ResponseEntity.status(201).body(res);
    }

    @GetMapping("/{id}")
    @Operation(summary = "시장 단건 조회", description = "ID로 시장 정보를 조회합니다.")
    public ResponseEntity<MarketDto.Response> get(@PathVariable Long id) {
        return ResponseEntity.ok(marketService.get(id));
    }

    @GetMapping
    @Operation(summary = "시장 목록 조회", description = "페이지 단위로 시장 목록을 조회합니다.")
    public ResponseEntity<Page<MarketDto.Response>> list(Pageable pageable) {
        return ResponseEntity.ok(marketService.list(pageable));
    }

    @PatchMapping("/{id}")
    @Operation(summary = "시장 부분 수정", description = "전달된 필드만 부분 업데이트합니다.")
    public ResponseEntity<MarketDto.Response> update(@PathVariable Long id,
                                                     @Valid @RequestBody MarketDto.UpdateRequest req) {
        return ResponseEntity.ok(marketService.update(id, req));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "시장 삭제", description = "해당 ID의 시장 정보를 삭제합니다.")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        marketService.delete(id);
        return ResponseEntity.noContent().build();
    }
}