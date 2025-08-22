package com.eiummarket.demo.repository;

import com.eiummarket.demo.model.Market;
import com.eiummarket.demo.model.MarketImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface MarketImageRepository extends JpaRepository<MarketImage, Long> {
    List<MarketImage> findByMarket_MarketId(Long itemId);
    List<MarketImage> findByMarket(Market market);
    void deleteByMarket_MarketId(Long marketId);
}