package com.eiummarket.demo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

public class ImageDto {

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    @Schema(name = "ImageUploadRequest", description = "이미지 업로드 요청 DTO")
    public static class UploadRequest {
        @Schema(description = "이미지 URL (이미 저장된 경우)", example = "https://example.com/img.png")
        private String imageUrl;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    @Schema(name = "ImageResponse", description = "이미지 응답 DTO")
    public static class Response {
        @NotNull
        @Schema(description = "이미지 ID", example = "1")
        private Long imageId;

        @NotBlank
        @Schema(description = "이미지 URL", example = "https://example.com/img.png")
        private String imageUrl;
    }
}
