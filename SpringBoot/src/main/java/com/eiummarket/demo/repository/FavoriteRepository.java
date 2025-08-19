package com.eiummarket.demo.repository;

import com.eiummarket.demo.model.Favorite;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
    boolean existsByShop_ShopIdAndUserId(Long shopId, Integer userId);
    Optional<Favorite> findByShop_ShopIdAndUserId(Long shopId, Integer userId);
    void deleteByShop_ShopIdAndUserId(Long shopId, Integer userId);
    Long countByShop_ShopId(Long shopId);
}