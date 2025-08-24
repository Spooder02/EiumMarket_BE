package com.eiummarket.demo.repository;

import com.eiummarket.demo.model.Category;
import com.eiummarket.demo.model.Shop;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.Optional;

public interface ShopRepository extends JpaRepository<Shop, Long> {

    boolean existsByShopIdAndMarket_MarketId(Long shopId, Long marketId);

    Optional<Shop> findByShopIdAndMarket_MarketId(Long shopId, Long marketId);

    Page<Shop> findAllByMarket_MarketId(Long marketId, Pageable pageable);

    Page<Shop> findAllByMarket_MarketIdAndCategoriesContaining(Long marketId, Category category, Pageable pageable);


    Page<Shop> findByNameContaining(String keyword, Pageable pageable);
    Page<Shop> findByDescriptionContaining(String keyword, Pageable pageable);
    Page<Shop> findByCategories_NameContaining(String categoryName, Pageable pageable);
    Page<Shop> findByMarket_MarketIdAndNameContaining(Long marketId, String keyword, Pageable pageable);
    Page<Shop> findByMarket_MarketIdAndDescriptionContaining(Long marketId, String keyword, Pageable pageable);

    @Modifying
    @Query("update Shop s set s.favoriteCount = s.favoriteCount + 1 where s.shopId = :shopId")
    void incrementFavoriteCount(@Param("shopId") Long shopId);

    @Modifying
    @Query("update Shop s set s.favoriteCount = s.favoriteCount - 1 where s.shopId = :shopId and s.favoriteCount > 0")
    void decrementFavoriteCount(@Param("shopId") Long shopId);

    // 필요 시 중복 이름 방지 등 추가 가능
    boolean existsByMarket_MarketIdAndName(Long marketId, String name);

}
