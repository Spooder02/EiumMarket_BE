package com.eiummarket.demo.repository;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.eiummarket.demo.model.Market;
import com.eiummarket.demo.domain.MarketNearbyProjection;

public interface MarketRepository extends JpaRepository<Market, Long> {
    boolean existsByName(String name);

    @Query(
        value =
        "SELECT " +
        "  m.market_id AS marketId, " +
        "  m.name AS name, " +
        "  m.address AS address, " +
        "  m.latitude AS latitude, " +
        "  m.longitude AS longitude, " +
        "  m.description AS description, " +
        "  m.created_at AS createdAt, " +
        "  (6371 * acos( " +
        "     cos(radians(:lat)) * cos(radians(m.latitude)) * " +
        "     cos(radians(m.longitude) - radians(:lon)) + " +
        "     sin(radians(:lat)) * sin(radians(m.latitude)) " +
        "   )) AS distanceKm " +
        "FROM market m " +
        "WHERE m.latitude BETWEEN :minLat AND :maxLat " +
        "  AND m.longitude BETWEEN :minLon AND :maxLon " +
        "HAVING distanceKm <= :radiusKm " +
        "ORDER BY distanceKm ASC",
        countQuery =
        "SELECT COUNT(*) " +
        "FROM market m " +
        "WHERE m.latitude BETWEEN :minLat AND :maxLat " +
        "  AND m.longitude BETWEEN :minLon AND :maxLon",
        nativeQuery = true
    )
    Page<MarketNearbyProjection> findNearby(
        @Param("lat") double lat,
        @Param("lon") double lon,
        @Param("minLat") double minLat,
        @Param("maxLat") double maxLat,
        @Param("minLon") double minLon,
        @Param("maxLon") double maxLon,
        @Param("radiusKm") double radiusKm,
        Pageable pageable
    );

    @Query(
        value = """
                SELECT m.*
                FROM market m
                WHERE (
                    LOWER(m.name) LIKE :pattern ESCAPE '\\\\'
                    OR LOWER(m.address) LIKE :pattern ESCAPE '\\\\'
                    OR LOWER(m.description) LIKE :pattern ESCAPE '\\\\'
                )
                """,
        countQuery = """
                SELECT COUNT(*)
                FROM market m
                WHERE (
                    LOWER(m.name) LIKE :pattern ESCAPE '\\\\'
                    OR LOWER(m.address) LIKE :pattern ESCAPE '\\\\'
                    OR LOWER(m.description) LIKE :pattern ESCAPE '\\\\'
                )
                """,
        nativeQuery = true
    )
    Page<Market> searchByKeyword(@Param("pattern") String pattern, Pageable pageable);

}