// src/main/java/com/eiummarket/demo/model/ItemImage.java
package com.eiummarket.demo.model;

import jakarta.persistence.*;
import lombok.*;

@Entity @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@Table(name = "item_image")
public class ItemImage {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "item_image_id")
    private Long itemImageId;

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "item_id", nullable = false)
    private Item item;

    @Column(nullable = false, length = 1000)
    private String url;
}
