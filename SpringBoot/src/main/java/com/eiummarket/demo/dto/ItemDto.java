package com.eiummarket.demo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

public class ItemDto {

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    @Schema(name = "ItemCreateRequest", description = "상품 생성 요청")
    public static class CreateRequest {
        @NotNull
        @Schema(description = "상점 ID", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
        private Long shopId;

        @NotBlank
        @Size(max = 255)
        @Schema(description = "상품 이름", example = "감자", requiredMode = Schema.RequiredMode.REQUIRED)
        private String name;

        @NotNull
        @Schema(description = "상품 가격", example = "1000", requiredMode = Schema.RequiredMode.REQUIRED)
        private Integer price;

        @Size(max = 100)
        @Schema(description = "상품 분류", example = "채소")
        private String category;

        @Schema(description = "상품 설명", example = "찌거나 볶아서 맛있는 감자입니다.")
        private String description;

        @Schema(description = "이미지 원격 URL 리스트")
        private List<@NotBlank String> imageUrls;

        @Schema(description = "업로드할 이미지 파일들")
        private List<MultipartFile> imageFiles;
    }


    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    @Schema(name = "ItemUpdateRequest", description = "상품 수정 요청")
    public static class UpdateRequest {
        @Size(max = 255) @Schema(description = "상품 이름", example = "감자")
        private String name;

        @Schema(description = "상품 가격", example = "1200")
        private Integer price;

        @Size(max = 100) @Schema(description = "상품 분류", example = "채소")
        private String category;

        @Schema(description = "상품 설명", example = "국거리로도 좋은 감자입니다.")
        private String description;

        @Schema(description = "이미지 파일들 (교체/추가)")
        private List<MultipartFile> imageFiles;

        @Schema(description = "이미지 URL 들 (교체/추가)")
        private List<String> imageUrls;

        @Schema(description = "삭제할 이미지 ID 리스트")
        private List<Long> imageIds;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    @Schema(name = "ItemResponse", description = "상품 응답 데이터")
    public static class Response {
        @Schema(description = "상품 고유 ID", example = "1")
        private Long itemId;

        @Schema(description = "상점 ID", example = "10")
        private Long shopId;

        @Schema(description = "상품 이름", example = "감자")
        private String name;
        @Schema(description = "상품 가격(정수, 원)", example = "1000")
        private Integer price;

        @Schema(description = "상품 분류", example = "채소")
        private String category;

        @Schema(description = "상품 설명", example = "찌거나 볶아서 맛있는 감자입니다.")
        private String description;

        @Schema(description = "상품 생성 시간", example = "2025-08-13T20:15:30")
        private LocalDateTime createdAt;

        @Schema(description = "상품 이미지 URL 목록")
        private List<String> imageUrls;
    }
}
