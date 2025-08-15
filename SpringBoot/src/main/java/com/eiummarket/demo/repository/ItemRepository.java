package com.eiummarket.demo.repository;

import com.eiummarket.demo.model.Item;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemRepository extends JpaRepository<Item, Long> {
    Page<Item> findAllByShop_ShopId(Long shopId, Pageable pageable);
}
