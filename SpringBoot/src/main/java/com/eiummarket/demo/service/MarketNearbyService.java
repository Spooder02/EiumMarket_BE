package com.eiummarket.demo.service;

import com.eiummarket.demo.dto.MarketDto;
import com.eiummarket.demo.domain.MarketNearbyProjection;
import com.eiummarket.demo.repository.MarketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MarketNearbyService {

    private final MarketRepository marketRepository;

    public Page<MarketDto.NearbyResponse> findNearby(double lat, double lon, double radiusKm, Pageable pageable) {
        // 바운딩 박스 계산
        double latDegreeKm = 111.32d;
        double lonDegreeKm = 111.32d * Math.cos(Math.toRadians(lat));

        double latDelta = radiusKm / latDegreeKm;
        double lonDelta = radiusKm / Math.max(lonDegreeKm, 1e-6); // 0 보호

        double minLat = lat - latDelta;
        double maxLat = lat + latDelta;
        double minLon = lon - lonDelta;
        double maxLon = lon + lonDelta;

        Page<MarketNearbyProjection> page = marketRepository.findNearby(
            lat, lon, minLat, maxLat, minLon, maxLon, radiusKm, pageable
        );

        return page.map(p -> MarketDto.NearbyResponse.builder()
                .marketId(p.getMarketId())
                .name(p.getName())
                .address(p.getAddress())
                .latitude(p.getLatitude())
                .longitude(p.getLongitude())
                .description(p.getDescription())
                .createdAt(p.getCreatedAt())
                .distanceKm(roundKm(p.getDistanceKm()))
                .build());
    }

    private Double roundKm(Double km) {
        if (km == null) return null;
        return Math.round(km * 1000.0) / 1000.0; // 소수점 셋째 자리까지
    }
}