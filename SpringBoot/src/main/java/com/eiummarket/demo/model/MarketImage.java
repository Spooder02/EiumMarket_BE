// src/main/java/com/eiummarket/demo/model/MarketImage.java
package com.eiummarket.demo.model;

import jakarta.persistence.*;
import lombok.*;

@Entity @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@Table(name = "market_image")
public class MarketImage {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "market_image_id")
    private Long marketImageId;

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "market_id", nullable = false)
    private Market market;

    @Column(nullable = false, length = 1000)
    private String url;
}
