package com.eiummarket.demo.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "category")
@Schema(name = "Category", description = "상품 카테고리를 나타내는 엔티티")

public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    @Schema(description = "카테고리 고유 ID", example = "1")
    private Long categoryId;

    @Column(nullable = false, length = 255)
    @Schema(description = "카테고리 이름", example = "채소", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    @ManyToMany(mappedBy = "categories")
    @Builder.Default
    private List<Shop> shops = new ArrayList<>();
}
