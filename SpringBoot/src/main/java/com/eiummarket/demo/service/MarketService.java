package com.eiummarket.demo.service;

import com.eiummarket.demo.dto.MarketDto;
import com.eiummarket.demo.model.Market;
import com.eiummarket.demo.repository.MarketRepository;

import lombok.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MarketService {

    private final MarketRepository marketRepository;

    @Transactional
    public MarketDto.Response create(MarketDto.CreateRequest req) {
        if (marketRepository.existsByName(req.getName())) {
            throw new IllegalArgumentException("이미 존재하는 시장 이름입니다.");
        }
        Market market = Market.builder()
                .name(req.getName())
                .address(req.getAddress())
                .latitude(req.getLatitude())
                .longitude(req.getLongitude())
                .description(req.getDescription())
                .build();
        Market saved = marketRepository.save(market);
        return toResponse(saved);
    }

    public MarketDto.Response get(Long id) {
        Market market = marketRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 시장을 찾을 수 없습니다."));
        return toResponse(market);
    }

    public Page<MarketDto.Response> list(Pageable pageable) {
        return marketRepository.findAll(pageable).map(this::toResponse);
    }

    @Transactional
    public MarketDto.Response update(Long id, MarketDto.UpdateRequest req) {
        Market market = marketRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 시장을 찾을 수 없습니다."));

        if (req.getName() != null) market.setName(req.getName());
        if (req.getAddress() != null) market.setAddress(req.getAddress());
        if (req.getLatitude() != null) market.setLatitude(req.getLatitude());
        if (req.getLongitude() != null) market.setLongitude(req.getLongitude());
        if (req.getDescription() != null) market.setDescription(req.getDescription());

        return toResponse(market);
    }

    @Transactional
    public void delete(Long id) {
        if (!marketRepository.existsById(id)) {
            throw new IllegalArgumentException("해당 ID의 시장을 찾을 수 없습니다.");
        }
        marketRepository.deleteById(id);
    }

    public Page<MarketDto.Response> search(String search, Pageable pageable) {
        String sanitized = sanitizeSearch(search);
        if (sanitized == null) {
            return list(pageable);
        }
        String pattern = "%" + escapeLike(sanitized.toLowerCase()) + "%";
        Page<Market> page = marketRepository.searchByKeyword(pattern, pageable);
        return page.map(this::toResponse);
    }

    private String sanitizeSearch(String s) {
        if (s == null) return null;
        String trimmed = s.trim();
        if (trimmed.isEmpty()) return null;
        // 길이 상한(예: 100자)으로 과도한 검색 방지
        return trimmed.length() > 100 ? trimmed.substring(0, 100) : trimmed;
    }

    // LIKE 검색용 이스케이프
    private String escapeLike(String s) {
        return s.replace("\\", "\\\\")
                .replace("%", "\\%")
                .replace("_", "\\_");
    }

    private MarketDto.Response toResponse(Market m) {
        return MarketDto.Response.builder()
                .marketId(m.getMarketId())
                .name(m.getName())
                .address(m.getAddress())
                .latitude(m.getLatitude())
                .longitude(m.getLongitude())
                .description(m.getDescription())
                .createdAt(m.getCreatedAt())
                .build();
    }
}