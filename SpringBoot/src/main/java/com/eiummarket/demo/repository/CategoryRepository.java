package com.eiummarket.demo.repository;


import com.eiummarket.demo.model.Shop;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.eiummarket.demo.model.Category;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    Optional<Category> findByName(String name);
    Optional<Category> findByCategoryId(Long categoryId);

    @Query("""
    select distinct s
      from Shop s
      join s.categories c
     where LOWER(c.name) like :pattern escape '\\'
""")
    Page<Shop> findShopsByCategoryNameLike(@Param("pattern") String pattern, Pageable pageable);

    @Query("" +
            "SELECT DISTINCT s " +
            "FROM Shop s " +
            "JOIN s.categories c " +
            "WHERE s.market.marketId = :marketId " +
            "AND c.name LIKE %:keyword%")
    Page<Shop> findShopsByMarketIdAndCategoryNameLike(@Param("marketId") Long marketId, @Param("keyword") String keyword, Pageable pageable);
}