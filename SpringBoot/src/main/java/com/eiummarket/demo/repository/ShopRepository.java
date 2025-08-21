package com.eiummarket.demo.repository;

import com.eiummarket.demo.model.Shop;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ShopRepository extends JpaRepository<Shop, Long> {

    Optional<Shop> findByShopIdAndMarket_MarketId(Long shopId, Long marketId);

    Page<Shop> findAllByMarket_MarketId(Long marketId, Pageable pageable);

    Page<Shop> findAllByMarket_MarketIdAndCategory(Long marketId, String category, Pageable pageable);

    // 즐겨찾기(찜) 사용자 필터로 상점 목록
    @Query("""
           select f.shop
             from Favorite f
            where f.userId = :userId
              and (:marketId is null or f.shop.market.marketId = :marketId)
           """)
    Page<Shop> findFavoriteShopsByUsername(@Param("userId") Integer userId,
                                           @Param("marketId") Long marketId,
                                           Pageable pageable);

    List<Shop> findByNameContainingIgnoreCase(String keyword);
    List<Shop> findByCategoryContainingIgnoreCase(String keyword);

    // 시장 내 특정 상점(소유 검증)
    Optional<Shop> findByMarket_MarketIdAndShopId(Long marketId, Long shopId);

    // 필요 시 중복 이름 방지 등 추가 가능
    boolean existsByMarket_MarketIdAndName(Long marketId, String name);

}
