package com.eiummarket.demo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

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

        @Schema(description = "이미지 원격 URL 리스트")
        private List<@NotBlank String> imageUrls;

        @Schema(description = "업로드할 이미지 파일들")
        private List<MultipartFile> imageFiles;
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

        @Schema(description = "이미지 파일들 (교체/추가 시)")
        private List<MultipartFile> imageFiles;

        @Schema(description = "이미지 URL 들 (교체/추가 시)")
        private List<String> imageUrls;

        @Schema(description = "삭제할 이미지 ID 리스트")
        private List<Long> imageIds;
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

        @Schema(description = "시장 이미지 URL 목록")
        private List<String> imageUrls;
    }
}