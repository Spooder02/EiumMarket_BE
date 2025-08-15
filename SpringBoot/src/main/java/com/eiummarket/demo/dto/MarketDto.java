package com.eiummarket.demo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class MarketDto {

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    @Schema(name = "MarketCreateRequest", description = "시장 생성 요청")
    public static class CreateRequest {
        @NotBlank
        @Size(max = 255)
        @Schema(description = "시장 이름", example = "남대문시장", requiredMode = Schema.RequiredMode.REQUIRED)
        private String name;

        @NotBlank
        @Size(max = 500)
        @Schema(description = "시장 주소", example = "서울특별시 중구 남대문시장4길 21", requiredMode = Schema.RequiredMode.REQUIRED)
        private String address;

        @Schema(description = "시장 위도(DECIMAL 9,6)", example = "37.559980")
        private BigDecimal latitude;

        @Schema(description = "시장 경도(DECIMAL 9,6)", example = "126.978400")
        private BigDecimal longitude;

        @Schema(description = "시장 설명", example = "전통 시장으로 다양한 먹거리와 상점이 밀집해 있습니다.")
        private String description;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    @Schema(name = "MarketUpdateRequest", description = "시장 수정 요청")
    public static class UpdateRequest {
        @Size(max = 255)
        @Schema(description = "시장 이름", example = "남대문시장")
        private String name;

        @Size(max = 500)
        @Schema(description = "시장 주소", example = "서울특별시 중구 남대문시장4길 21")
        private String address;

        @Schema(description = "시장 위도(DECIMAL 9,6)", example = "37.559980")
        private BigDecimal latitude;

        @Schema(description = "시장 경도(DECIMAL 9,6)", example = "126.978400")
        private BigDecimal longitude;

        @Schema(description = "시장 설명", example = "전통 시장으로 다양한 먹거리와 상점이 밀집해 있습니다.")
        private String description;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    @Schema(name = "MarketResponse", description = "시장 응답 데이터")
    public static class Response {
        @Schema(description = "시장 고유 ID", example = "1")
        private Long marketId;

        @Schema(description = "시장 이름", example = "남대문시장")
        private String name;

        @Schema(description = "시장 주소", example = "서울특별시 중구 남대문시장4길 21")
        private String address;

        @Schema(description = "시장 위도(DECIMAL 9,6)", example = "37.559980")
        private BigDecimal latitude;

        @Schema(description = "시장 경도(DECIMAL 9,6)", example = "126.978400")
        private BigDecimal longitude;

        @Schema(description = "시장 설명", example = "전통 시장으로 다양한 먹거리와 상점이 밀집해 있습니다.")
        private String description;

        @Schema(description = "시장 정보 생성 일시", example = "2025-08-13T20:15:30")
        private LocalDateTime createdAt;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    @Schema(name = "MarketNearbyResponse", description = "근처 시장 응답(거리 포함)")
    public static class NearbyResponse {
        @Schema(description = "시장 고유 ID", example = "1")
        private Long marketId;

        @Schema(description = "시장 이름", example = "남대문시장")
        private String name;

        @Schema(description = "시장 주소", example = "서울특별시 중구 남대문시장4길 21")
        private String address;

        @Schema(description = "위도", example = "37.559980")
        private BigDecimal latitude;

        @Schema(description = "경도", example = "126.978400")
        private BigDecimal longitude;

        @Schema(description = "설명")
        private String description;

        @Schema(description = "생성 일시", example = "2025-08-13T20:15:30")
        private LocalDateTime createdAt;

        @Schema(description = "요청 좌표로부터의 거리(km)", example = "1.23")
        private Double distanceKm;
    }
}