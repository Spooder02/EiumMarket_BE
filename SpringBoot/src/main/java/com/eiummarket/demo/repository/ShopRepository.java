package com.eiummarket.demo.repository;

import com.eiummarket.demo.model.Shop;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ShopRepository extends JpaRepository<Shop, Long> {

    Optional<Shop> findByShopIdAndMarketId(Long shopId, Long marketId);

    Page<Shop> findAllByMarketId(Long marketId, Pageable pageable);
}
