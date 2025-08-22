package com.eiummarket.demo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

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

        @Schema(description = "카테고리 ID 목록", example = "[1,2]")
        private List<Long> categoryIds;

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

        @Schema(description = "상점 위치", example = "전남 여수시 서교4길 8-3")
        private String address;

        @Schema(description = "이미지 원격 URL 리스트")
        private List<@NotBlank String> imageUrls;

        @Schema(description = "업로드할 이미지 파일들")
        private List<MultipartFile> imageFiles;

    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    @Schema(name = "ShopUpdateRequest", description = "상점 수정 요청")
    public static class UpdateRequest {
        @Size(max = 255)
        @Schema(description = "상점 이름", example = "김밥천국")
        private String name;

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

        @Schema(description = "상점 위치", example = "전남 여수시 서교4길 8-3")
        private String address;

        @Schema(description = "카테고리 ID 목록", example = "[1,2]")
        private List<Long> categoryIds;

        @Schema(description = "이미지 파일들 (교체/추가)")
        private List<MultipartFile> imageFiles;

        @Schema(description = "이미지 URL 들 (교체/추가)")
        private List<String> imageUrls;

        @Schema(description = "삭제할 이미지 ID 리스트")
        private List<Long> imageIds;

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

        @Schema(description = "상점 전화번호", example = "02-123-4567")
        private String phoneNumber;

        @Schema(description = "운영 시간", example = "09:00 ~ 21:00")
        private String openingHours;

        @Schema(description = "상점 주소", example = "전남 여수시 서교4길 8-3")
        private String address;

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

        @Schema(description = "찜한 사용자 수", example = "5")
        private Long favoriteCount;

        @Schema(description = "검색 시 매칭된 키워드")
        private List<String> matchedKeywords;

        @Schema(description = "상점에서 판매하는 상품 목록")
        private List<ItemDto.Response> items;

        @Schema(description = "카테고리 목록")
        private List<CategoryDto.Response> categories;

        @Schema(description = "이미지 원격 URL 리스트")
        private List<String> imageUrls;

    }

}