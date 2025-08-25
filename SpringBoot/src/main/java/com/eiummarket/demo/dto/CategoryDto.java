package com.eiummarket.demo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

public class CategoryDto {

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    @Schema(name = "CategoryCreateRequest", description = "카테고리 생성 요청")
    public static class CreateRequest {
        @NotBlank
        @Size(max = 50)
        @Schema(description = "카테고리 이름", example = "채소", requiredMode = Schema.RequiredMode.REQUIRED)
        private String name;

        private String icon;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    @Schema(name = "CategoryResponse", description = "카테고리 응답 데이터")
    public static class Response {
        @NotNull
        @Schema(description = "카테고리 ID", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
        private Long categoryId;

        @NotBlank
        @Schema(description = "카테고리 이름", example = "채소", requiredMode = Schema.RequiredMode.REQUIRED)
        private String name;

        private String icon;
    }
}