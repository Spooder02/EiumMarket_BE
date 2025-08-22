package com.eiummarket.demo.repository;

import com.eiummarket.demo.model.ItemImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ItemImageRepository extends JpaRepository<ItemImage, Long> {
    List<ItemImage> findByItem_ItemId(Long itemId);
    void deleteByItem_ItemId(Long itemId);
}