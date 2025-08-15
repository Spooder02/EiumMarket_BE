package com.eiummarket.demo.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "item")
@Schema(name = "Item", description = "상품 정보를 나타내는 엔티티")
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "item_id")
    @Schema(description = "상품 고유 ID", example = "1")
    private Long itemId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shop_id", nullable = false)
    @Schema(description = "상품이 속한 상점", requiredMode = Schema.RequiredMode.REQUIRED)
    private Market shop;

    @Column(nullable = false, length = 255)
    @Schema(description = "상품 이름", example = "감자", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    @Column(nullable = false)
    @Schema(description = "상품 가격", example = "1000", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer price;

    @Column(length = 100)
    @Schema(description = "상품 분류 (예: 채소, 고기 등)", example = "채소")
    private String category;

    @Lob
    @Column(name = "description", columnDefinition = "TEXT")
    @Schema(description = "상품 설명", example = "찌거나 볶아서 맛있는 감자입니다. ")
    private String description;

    @Column(name="created_at")
    @CreationTimestamp
    @Schema(description = "상품 생성 시간", example = "2025-08-13T20:15:30")
    private LocalDateTime createdAt;

}
