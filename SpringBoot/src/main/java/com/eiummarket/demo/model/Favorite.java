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
@Table(name = "favorite",
        uniqueConstraints = @UniqueConstraint(name = "uk_favorite_shop_user", columnNames = {"shop_id","username_id"}))
@Schema(name = "Favorite", description = "사용자가 찜한 가게를 나타내는 엔티티")
public class Favorite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "favorite")
    @Schema(description = "즐겨찾기 고유 ID", example = "1")
    private Long favoriteId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shop_id", nullable = false)
    @Schema(description = "찜한 상점", requiredMode = Schema.RequiredMode.REQUIRED)
    private Shop shop;

    @Column(name = "username_id", nullable = false, length = 100)
    @Schema(description = "사용자 식별 id", example = "1234")
    private Integer userId;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false,
            columnDefinition = "timestamp default current_timestamp")
    @Schema(description = "생성 일시", example = "2025-08-13T20:15:30")
    private LocalDateTime createdAt;
}
