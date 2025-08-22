// src/main/java/com/eiummarket/demo/repository/ReviewImageRepository.java
package com.eiummarket.demo.repository;

import com.eiummarket.demo.model.ReviewImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewImageRepository extends JpaRepository<ReviewImage, Long> {
    List<ReviewImage> findByReview_ReviewId(Long reviewId);
}
