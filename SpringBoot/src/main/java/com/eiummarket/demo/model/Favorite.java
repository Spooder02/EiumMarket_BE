package com.eiummarket.demo.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "favorite")
@Schema(name = "Favorite", description = "찜한 가게를 나타내는 엔티티")
public class Favorite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "favorite_id")
    @Schema(description = "즐겨찾기 고유 ID", example = "1")
    private Long favoriteId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shop_id", nullable = false)
    @Schema(description = "찜한 상점", requiredMode = Schema.RequiredMode.REQUIRED)
    private Shop shop;

    @Column(name = "favorite_count", nullable = false)
    @Schema(description = "찜 횟수", example = "0")
    private Long favoriteCount;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false,
            columnDefinition = "timestamp default current_timestamp")
    @Schema(description = "생성 일시", example = "2025-08-13T20:15:30")
    private LocalDateTime createdAt;
}
