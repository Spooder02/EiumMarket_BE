package com.eiummarket.demo.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
    @JsonIgnore // 순환 참조 방지용(응답에서 market 필드를 숨기고 싶을 때)
    private Market market;

    // 선택: 조회 편의를 위한 읽기 전용 FK
    @Column(name = "market_id", insertable = false, updatable = false)
    @Schema(description = "상점이 속한 시장 ID", example = "1")
    private Long marketId;

    @Column(nullable = false, length = 255)
    private String name;

    @Column(length = 100)
    private String category;

    @Column(name = "phone_number", length = 20)
    private String phoneNumber;

    @Column(name = "opening_hours", length = 255)
    private String openingHours;

    @Column(length = 50)
    private String floor;

    @Column(precision = 9, scale = 6)
    private BigDecimal latitude;

    @Column(precision = 9, scale = 6)
    private BigDecimal longitude;

    @Lob
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false, columnDefinition = "timestamp default current_timestamp")
    private LocalDateTime createdAt;
}