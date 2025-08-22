// src/main/java/com/eiummarket/demo/model/ShopImage.java
package com.eiummarket.demo.model;

import jakarta.persistence.*;
import lombok.*;

@Entity @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@Table(name = "shop_image")
public class ShopImage {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "shop_image_id")
    private Long shopImageId;

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "shop_id", nullable = false)
    private Shop shop;

    @Column(nullable = false, length = 1000)
    private String url;
}
