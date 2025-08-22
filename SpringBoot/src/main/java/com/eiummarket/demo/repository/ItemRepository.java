package com.eiummarket.demo.repository;

import com.eiummarket.demo.model.Item;
import com.eiummarket.demo.model.Shop;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
    Page<Item> findAllByShop_ShopId(Long shopId, Pageable pageable)
            ;
    boolean existsByItemIdAndShop_ShopId(Long itemId, Long shopId);
    List<Item> findByNameContainingOrDescriptionContaining(String name, String description);

    @Query("""
    select distinct i.shop
      from Item i
     where LOWER(i.name) like :pattern escape '\\'
        or LOWER(cast(i.description as string)) like :pattern escape '\\'
""")
    Page<Shop> findShopsByItemKeywordLike(@Param("pattern") String pattern, Pageable pageable);

    @Query("" +
            "SELECT DISTINCT i.shop " +
            "FROM Item i " +
            "WHERE i.shop.market.marketId = :marketId " +
            "AND (i.name LIKE %:keyword% " +
            "OR i.description LIKE %:keyword%)")
    Page<Shop> findShopsByMarketIdAndItemKeyword(@Param("marketId") Long marketId, @Param("keyword") String keyword, Pageable pageable);
}