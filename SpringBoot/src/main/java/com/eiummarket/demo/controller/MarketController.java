package com.eiummarket.demo.controller;

import com.eiummarket.demo.dto.MarketDto;
import com.eiummarket.demo.service.MarketNearbyService;
import com.eiummarket.demo.service.MarketService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/markets")
@RequiredArgsConstructor
@Tag(name = "Market API", description = "시장 정보 CRUD API")
public class MarketController {

    private final MarketService marketService;

    private final MarketNearbyService marketNearbyService;

    @PostMapping
    @Operation(summary = "시장 등록하기", description = "시장 정보를 신규 등록합니다.")
    @ApiResponse(responseCode = "201", description = "생성 성공",
            content = @Content(schema = @Schema(implementation = MarketDto.Response.class)))
    public ResponseEntity<MarketDto.Response> create(@Valid @RequestBody MarketDto.CreateRequest req) {
        MarketDto.Response res = marketService.create(req);
        return ResponseEntity.status(201).body(res);
    }

    @GetMapping("/{marketId}")
    @Operation(summary = "시장 단일 조회", description = "ID로 시장 정보를 조회합니다.")
    public ResponseEntity<MarketDto.Response> get(@PathVariable Long marketId) {
        return ResponseEntity.ok(marketService.get(marketId));
    }

    @GetMapping(params = "!search")
    @Operation(summary = "시장 목록 조회", description = "페이지 단위로 시장 목록을 조회합니다.")
    public ResponseEntity<Page<MarketDto.Response>> list(Pageable pageable) {
        return ResponseEntity.ok(marketService.list(pageable));
    }

    @PatchMapping("/{marketId}")
    @Operation(summary = "시장 부분 수정", description = "전달된 필드만 부분 업데이트합니다.")
    public ResponseEntity<MarketDto.Response> update(@PathVariable Long marketId,
                                                     @Valid @RequestBody MarketDto.UpdateRequest req) {
        return ResponseEntity.ok(marketService.update(marketId, req));
    }

    @DeleteMapping("/{marketId}")
    @Operation(summary = "시장 삭제", description = "해당 ID의 시장 정보를 삭제합니다.")
    public ResponseEntity<Void> delete(@PathVariable Long marketId) {
        marketService.delete(marketId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/nearby")
    @Operation(summary = "근처 시장 조회", description = "요청 좌표(lat, lon) 기준 반경(radiusKm) 내 시장을 거리(km) 오름차순으로 반환합니다.")
    public ResponseEntity<Page<MarketDto.NearbyResponse>> nearby(
            @Parameter(description = "위도", required = true, example = "37.5665")
            @RequestParam("lat") double lat,
            @Parameter(description = "경도", required = true, example = "126.9780")
            @RequestParam("lon") double lon,
            @Parameter(description = "반경(km)", example = "5")
            @RequestParam(value = "radiusKm", defaultValue = "5") double radiusKm,
            @ParameterObject Pageable pageable
    ) {
        Pageable sorted = pageable;
        if (pageable.getSort().isUnsorted()) {
            sorted = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(),
                    Sort.by(Sort.Direction.ASC, "distanceKm"));
        }
        return ResponseEntity.ok(marketNearbyService.findNearby(lat, lon, radiusKm, sorted));
    }

    @GetMapping
    @Operation(summary = "시장 검색/목록", description = "search 파라미터가 있으면 이름/주소/설명에서 부분 검색 후 페이지네이션하여 반환합니다. 파라미터가 없으면 전체 목록을 반환합니다.")
    public ResponseEntity<Page<MarketDto.Response>> searchOrList(
            @Parameter(description = "검색어(대소문자 무시, 부분 일치)", example = "남대문")
            @RequestParam(value = "search", required = false) String search,
            @ParameterObject Pageable pageable
    ) {
        if (search == null || search.trim().isEmpty()) {
            return ResponseEntity.ok(marketService.list(pageable));
        }
        return ResponseEntity.ok(marketService.search(search, pageable));
    }
}