// src/main/java/com/eiummarket/demo/model/ReviewImage.java
package com.eiummarket.demo.model;

import jakarta.persistence.*;
import lombok.*;

@Entity @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@Table(name = "review_image")
public class ReviewImage {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_image_id")
    private Long reviewImageId;

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "review_id", nullable = false)
    private Review review;

    @Column(nullable = false, length = 1000)
    private String url;
}
