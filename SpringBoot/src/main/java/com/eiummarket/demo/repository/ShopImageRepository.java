package com.eiummarket.demo.repository;

import com.eiummarket.demo.model.Shop;
import com.eiummarket.demo.model.ShopImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ShopImageRepository extends JpaRepository<ShopImage, Long> {
    List<ShopImage> findByShop_ShopId(Long shopId);
    void deleteByShop_ShopId(Shop shopId);
}