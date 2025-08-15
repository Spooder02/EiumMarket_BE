package com.eiummarket.demo.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public interface MarketNearbyProjection {
    Long getMarketId();
    String getName();
    String getAddress();
    BigDecimal getLatitude();
    BigDecimal getLongitude();
    String getDescription();
    LocalDateTime getCreatedAt();
    Double getDistanceKm();
}