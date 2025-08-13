package com.eiummarket.demo.repository;
import org.springframework.data.jpa.repository.JpaRepository;

import com.eiummarket.demo.model.Market;

public interface MarketRepository extends JpaRepository<Market, Long> {
    boolean existsByName(String name);
}