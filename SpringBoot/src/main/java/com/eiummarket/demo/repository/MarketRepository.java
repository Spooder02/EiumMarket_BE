package com.eiummarket.demo.repository;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.eiummarket.demo.model.Market;

public interface MarketRepository extends JpaRepository<Market, Long> {
    boolean existsByName(String name);
    Page<Market> findMarketByNameContaining(String keyword, Pageable pageable);
    Page<Market> findMarketByDescriptionContaining(String keyword, Pageable pageable);

    Page<Market> findAll(Pageable pageable);

    Optional<Market> findByNameOrAddress(String name, String address);
}