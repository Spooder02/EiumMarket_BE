package com.eiummarket.demo.repository;

import com.eiummarket.demo.model.Favorite;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
    Optional<Favorite> findByShop_ShopIdAndShop_Market_MarketId(Long shopId, Long marketId);
    Page<Favorite> findByShop_Market_MarketId(Long marketId, Pageable pageable);
}