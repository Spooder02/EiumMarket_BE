// src/main/java/com/eiummarket/demo/repository/ReviewRepository.java
package com.eiummarket.demo.repository;

import com.eiummarket.demo.model.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    Page<Review> findByShop_ShopId(Long shopId, Pageable pageable);
    Page<Review> findByItem_ItemId(Long itemId, Pageable pageable);
}
