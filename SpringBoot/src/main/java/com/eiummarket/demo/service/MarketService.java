package com.eiummarket.demo.service;

import com.eiummarket.demo.dto.MarketDto;
import com.eiummarket.demo.model.Market;
import com.eiummarket.demo.model.MarketImage;
import com.eiummarket.demo.repository.MarketImageRepository;
import com.eiummarket.demo.repository.MarketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityNotFoundException;
import java.util.stream.Collectors;

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

        if (req.getImageFiles() != null) {
            for (MultipartFile f : req.getImageFiles()) {
                String url = fileStorageService.storeFile(f);
                marketImageRepository.save(MarketImage.builder()
                        .market(saved)
                        .url(url)
                        .build());
            }
        }
        if (req.getImageUrls() != null) {
            for (String url : req.getImageUrls()) {
                marketImageRepository.save(MarketImage.builder()
                        .market(saved)
                        .url(url)
                        .build());
            }
        }
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
