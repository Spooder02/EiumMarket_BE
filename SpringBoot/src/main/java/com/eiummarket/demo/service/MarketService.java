package com.eiummarket.demo.service;

import com.eiummarket.demo.Utils.SearchUtils;
import com.eiummarket.demo.dto.MarketDto;
import com.eiummarket.demo.model.Market;
import com.eiummarket.demo.model.MarketImage;
import com.eiummarket.demo.model.Shop;
import com.eiummarket.demo.repository.MarketImageRepository;
import com.eiummarket.demo.repository.MarketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import jakarta.persistence.EntityNotFoundException;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.eiummarket.demo.Utils.SearchUtils.addAllMarkets;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MarketService {

    private final MarketRepository marketRepository;
    private final MarketImageRepository marketImageRepository;
    private final FileStorageService fileStorageService;

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

        // 이미지 파일 개별 추가
        if (req.getImageFiles() != null) {
            for (MultipartFile f : req.getImageFiles()) {
                String url = fileStorageService.storeFile(f);
                if (url != null) market.getImages().add(MarketImage.builder().market(market).url(url).build());
            }
        }
        // 이미지 URL 개별 추가
        if (req.getImageUrls() != null) {
            for (String url : req.getImageUrls()) {
                boolean exists = market.getImages().stream().anyMatch(img -> img.getUrl().equals(url));
                if (!exists) market.getImages().add(MarketImage.builder().market(market).url(url).build());
            }
        }
        return toResponse(saved);
    }

    public MarketDto.Response get(Long id) {
        Market market = marketRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 시장을 찾을 수 없습니다."));
        return toResponse(market);
    }

    public String getMarketNameById(Long marketId) {
        Market market = marketRepository.findById(marketId)
                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 시장을 찾을 수 없습니다."));
        return market.getName();
    }

    public Page<MarketDto.Response> list(Pageable pageable) {
        return marketRepository.findAll(pageable).map(this::toResponse);
    }

    @Transactional
    public MarketDto.Response update(Long marketId, MarketDto.UpdateRequest req) {
        Market market = marketRepository.findById(marketId)
                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 시장을 찾을 수 없습니다."));
        if (req.getName() != null) market.setName(req.getName());
        if (req.getAddress() != null) market.setAddress(req.getAddress());
        if (req.getLatitude() != null) market.setLatitude(req.getLatitude());
        if (req.getLongitude() != null) market.setLongitude(req.getLongitude());
        if (req.getDescription() != null) market.setDescription(req.getDescription());

        // 이미지 개별 삭제
        if (req.getImageIds() != null) {
            market.getImages().removeIf(img -> req.getImageIds().contains(img.getMarketImageId()));
        }
        // 이미지 파일 개별 추가
        if (req.getImageFiles() != null) {
            for (MultipartFile f : req.getImageFiles()) {
                String url = fileStorageService.storeFile(f);
                if (url != null) market.getImages().add(MarketImage.builder().market(market).url(url).build());
            }
        }
        // 이미지 URL 개별 추가
        if (req.getImageUrls() != null) {
            for (String url : req.getImageUrls()) {
                boolean exists = market.getImages().stream().anyMatch(img -> img.getUrl().equals(url));
                if (!exists) market.getImages().add(MarketImage.builder().market(market).url(url).build());
            }
        }
        return toResponse(market);
    }

    @Transactional
    public void delete(Long marketId) {
        Market market = marketRepository.findById(marketId)
                .orElseThrow(() -> new EntityNotFoundException("해당 시장을 찾을 수 없습니다. ID=" + marketId));
        // 관련된 상점, 상품, 리뷰 등이 모두 삭제됨 (cascade = CascadeType.ALL)
        marketRepository.delete(market);
    }

    @Transactional
    public Page<MarketDto.Response> searchMarkets(String keyword, Pageable pageable){
        String sanitized = SearchUtils.sanitize(keyword);
        if (sanitized == null) {
            return marketRepository.findAll(pageable).map(this::toResponse);
        }
        sanitized = sanitized.replace("%", "").replace("_", "").trim();
        if (!org.springframework.util.StringUtils.hasText(sanitized)) {
            return marketRepository.findAll(pageable).map(this::toResponse);
        }

        List<Market> ByName= marketRepository.findMarketByNameContaining(sanitized, pageable).getContent();
        List<Market> ByDesc = marketRepository.findMarketByDescriptionContaining(sanitized, pageable).getContent();

        Map<Long, Market> merged = new LinkedHashMap<>();
        addAllMarkets(merged, ByDesc);
        addAllMarkets(merged, ByName);

        List<Market> all = new ArrayList<>(merged.values());
        SearchUtils.sortByPageableMarket(all, pageable);

        int total=all.size();
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), total);
        List<Market> slice = (start >= total) ? List.of() : all.subList(start, end);

        return new PageImpl<>(slice.stream().map(this::toResponse).toList(), pageable, total);


    }

    @Transactional
    public boolean checkMarketExistence(String name, String address) {
        return marketRepository.findByNameOrAddress(name, address).isPresent();
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
                .imageUrls(m.getImages().stream()
                        .map(MarketImage::getUrl)
                        .collect(Collectors.toList()))
                .build();
    }
}
