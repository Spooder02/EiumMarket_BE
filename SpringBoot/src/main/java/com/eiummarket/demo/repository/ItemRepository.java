package com.eiummarket.demo.repository;

import com.eiummarket.demo.model.Item;
import com.eiummarket.demo.model.Shop;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;

public interface ItemRepository extends JpaRepository<Item, Long> {
    Page<Item> findAllByShop_ShopId(Long shopId, Pageable pageable);

    @Query("""
    select distinct i.shop
      from Item i
     where LOWER(i.name) like :pattern escape '\\'
        or LOWER(cast(i.description as string)) like :pattern escape '\\'
""")
    Page<Shop> findShopsByItemKeywordLike(@Param("pattern") String pattern, Pageable pageable);
}