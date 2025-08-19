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
    Page<Shop> findFavoriteShopsByUserId(@Param("userId") Integer userId,
                                           @Param("marketId") Long marketId,
                                           Pageable pageable);

    @Query("SELECT s FROM Shop s WHERE " +
            "LOWER(s.name) LIKE %:keyword% OR " +
            "LOWER(s.category) LIKE %:keyword% OR " +
            "LOWER(s.description) LIKE %:keyword% OR " +
            "EXISTS (SELECT i FROM Item i WHERE i.shop = s AND (LOWER(i.name) LIKE %:keyword% OR LOWER(i.description) LIKE %:keyword%))")
    Page<Shop> searchByAllKeywords(@Param("keyword") String keyword, Pageable pageable);

}