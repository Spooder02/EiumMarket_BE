// src/main/java/com/eiummarket/demo/dto/ReviewDto.java
package com.eiummarket.demo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

public class ReviewDto {

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    @Schema(name = "ReviewCreateRequest", description = "리뷰 생성 요청(Shop 또는 Item 중 하나 지정)")
    public static class CreateRequest {
        @NotNull @Schema(description = "작성자(사용자) device ID", example = "1234", requiredMode = Schema.RequiredMode.REQUIRED)
        private Integer userDeviceId;

        @Schema(description = "리뷰 대상 Shop ID")
        private Long shopId;

        @Schema(description = "리뷰 대상 Item ID")
        private Long itemId;

        @NotNull @Min(1) @Max(5)
        private Integer rating;

        @Size(max = 2000)
        private String content;

        @Schema(description = "이미지 원격 URL 리스트")
        private List<@NotBlank String> imageUrls;

        @Schema(description = "업로드할 이미지 파일들")
        private List<MultipartFile> imageFiles;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    @Schema(name = "ReviewUpdateRequest", description = "리뷰 수정 요청")
    public static class UpdateRequest {
        @Min(1) @Max(5)
        private Integer rating;

        @Size(max = 2000)
        private String content;

        @Schema(description = "이미지 원격 URL 리스트")
        private List<@NotBlank String> imageUrls;

        @Schema(description = "업로드할 이미지 파일들")
        private List<MultipartFile> imageFiles;

        @Schema(description = "삭제할 이미지 ID 리스트")
        private List<Long> imageIds;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    @Schema(name = "ReviewResponse", description = "리뷰 응답 데이터")
    public static class Response {
        private Long reviewId;
        private Integer userDeviceId;
        private Integer rating;
        private String content;
        private Long shopId;
        private Long itemId;
        private LocalDateTime createdAt;
        @Schema(description = "리뷰 이미지 URL 목록")
        private List<String> imageUrls;
    }
}
