package com.eiummarket.demo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ShopDto {

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    @Schema(name = "ShopCreateRequest", description = "상점 생성 요청")
    public static class CreateRequest {
        @NotNull
        @Schema(description = "시장 ID", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
        private Long marketId;

        @NotBlank
        @Size(max = 255)
        @Schema(description = "상점 이름", example = "김밥천국", requiredMode = Schema.RequiredMode.REQUIRED)
        private String name;

        @Size(max = 100)
        @Schema(description = "상점 분류", example = "음식점")
        private String category;

        @Size(max = 20)
        @Schema(description = "상점 전화번호", example = "02-123-4567")
        private String phoneNumber;

        @Size(max = 255)
        @Schema(description = "운영 시간", example = "09:00 ~ 21:00")
        private String openingHours;

        @Size(max = 50)
        @Schema(description = "상점 위치/층수", example = "A-02호")
        private String floor;

        @Schema(description = "상점 위도", example = "37.559980")
        private BigDecimal latitude;

        @Schema(description = "상점 경도", example = "126.978400")
        private BigDecimal longitude;

        @Schema(description = "상점 설명", example = "다양한 메뉴와 저렴한 가격이 장점입니다.")
        private String description;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    @Schema(name = "ShopUpdateRequest", description = "상점 수정 요청")
    public static class UpdateRequest {
        @Size(max = 255)
        @Schema(description = "상점 이름", example = "김밥천국")
        private String name;

        @Size(max = 100)
        @Schema(description = "상점 분류", example = "음식점")
        private String category;

        @Size(max = 20)
        @Schema(description = "상점 전화번호", example = "02-123-4567")
        private String phoneNumber;

        @Size(max = 255)
        @Schema(description = "운영 시간", example = "09:00 ~ 21:00")
        private String openingHours;

        @Size(max = 50)
        @Schema(description = "상점 위치/층수", example = "A-02호")
        private String floor;

        @Schema(description = "상점 위도", example = "37.559980")
        private BigDecimal latitude;

        @Schema(description = "상점 경도", example = "126.978400")
        private BigDecimal longitude;

        @Schema(description = "상점 설명", example = "다양한 메뉴와 저렴한 가격이 장점입니다.")
        private String description;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    @Schema(name = "ShopResponse", description = "상점 응답 데이터")
    public static class Response {
        @Schema(description = "상점 고유 ID", example = "1")
        private Long shopId;

        @Schema(description = "시장 ID", example = "1")
        private Long marketId;

        @Schema(description = "상점 이름", example = "김밥천국")
        private String name;

        @Schema(description = "상점 분류", example = "음식점")
        private String category;

        @Schema(description = "상점 전화번호", example = "02-123-4567")
        private String phoneNumber;

        @Schema(description = "운영 시간", example = "09:00 ~ 21:00")
        private String openingHours;

        @Schema(description = "상점 위치/층수", example = "A-02호")
        private String floor;

        @Schema(description = "상점 위도", example = "37.559980")
        private BigDecimal latitude;

        @Schema(description = "상점 경도", example = "126.978400")
        private BigDecimal longitude;

        @Schema(description = "상점 설명", example = "다양한 메뉴와 저렴한 가격이 장점입니다.")
        private String description;

        @Schema(description = "상점 정보 생성 일시", example = "2025-08-13T20:15:30")
        private LocalDateTime createdAt;
    }
}
