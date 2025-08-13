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
@Table(name = "market")
@Schema(name = "Market", description = "시장 정보를 나타내는 엔티티")
public class Market {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "market_id")
    @Schema(description = "시장 고유 ID", example = "1")
    private Long marketId;

    @Column(name = "name", nullable = false, length = 255)
    @Schema(description = "시장 이름", example = "남대문시장", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    @Column(name = "address", nullable = false, length = 500)
    @Schema(description = "시장 주소", example = "서울특별시 중구 남대문시장4길 21", requiredMode = Schema.RequiredMode.REQUIRED)
    private String address;

    @Column(name = "latitude", precision = 9, scale = 6)
    @Schema(description = "시장 위도(DECIMAL 9,6)", example = "37.559980")
    private BigDecimal latitude;

    @Column(name = "longitude", precision = 9, scale = 6)
    @Schema(description = "시장 경도(DECIMAL 9,6)", example = "126.978400")
    private BigDecimal longitude;

    @Lob
    @Column(name = "description", columnDefinition = "TEXT")
    @Schema(description = "시장 설명", example = "전통 시장으로 다양한 먹거리와 상점이 밀집해 있습니다.")
    private String description;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false, columnDefinition = "timestamp default current_timestamp")
    @Schema(description = "시장 정보 생성 일시", example = "2025-08-13T20:15:30")
    private LocalDateTime createdAt;
}