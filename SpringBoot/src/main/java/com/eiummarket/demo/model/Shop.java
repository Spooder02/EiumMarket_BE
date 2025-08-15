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
@Table(name = "shop")
@Schema(name = "Shop", description = "상점 정보를 나타내는 엔티티")
public class Shop {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "shop_id")
    @Schema(description = "상점 고유 ID", example = "1")
    private Long shopId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "market_id", nullable = false)
    @Schema(description = "상점이 속한 시장", requiredMode = Schema.RequiredMode.REQUIRED)
    private Market market;

    @Column(nullable = false, length = 255)
    @Schema(description = "상점 이름", example = "김밥천국", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    @Column(length = 100)
    @Schema(description = "상점 분류 (예: 음식점, 의류 등)", example = "음식점")
    private String category;

    @Column(name = "phone_number", length = 20)
    @Schema(description = "상점 전화번호", example = "02-123-4567")
    private String phoneNumber;

    @Column(name = "opening_hours", length = 255)
    @Schema(description = "운영 시간", example = "09:00 ~ 21:00")
    private String openingHours;

    @Column(length = 50)
    @Schema(description = "상점 위치/층수", example = "A-02호")
    private String floor;

    @Column(precision = 9, scale = 6)
    @Schema(description = "상점 위도", example = "37.559980")
    private BigDecimal latitude;

    @Column(precision = 9, scale = 6)
    @Schema(description = "상점 경도", example = "126.978400")
    private BigDecimal longitude;

    @Lob
    @Column(name = "description", columnDefinition = "TEXT")
    @Schema(description = "상점 설명", example = "다양한 메뉴와 저렴한 가격이 장점입니다.")
    private String description;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false, columnDefinition = "timestamp default current_timestamp")
    @Schema(description = "상점 정보 생성 일시", example = "2025-08-13T20:15:30")
    private LocalDateTime createdAt;
}
