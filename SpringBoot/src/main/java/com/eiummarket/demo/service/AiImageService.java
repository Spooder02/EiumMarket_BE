package com.eiummarket.demo.service;

import com.eiummarket.demo.exception.AiServerException;
import com.eiummarket.demo.service.util.ByteArrayMultipartFile;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AiImageService {

    private final WebClient webClient;
    private final FileStorageService fileStorageService;

    @Value("${ai.flask.server.url}")
    private String flaskServerUrl;

    public enum AiImageDomain {
        PRODUCT,
        SHOP
    }

    public String generateAndStoreImage(
            AiImageDomain domain,
            String itemName,
            String marketName,
            String title,
            String description) {

        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(flaskServerUrl);

        switch (domain) {
            case PRODUCT:
                if (!StringUtils.hasText(itemName)) {
                    throw new IllegalArgumentException("'itemName' 파라미터가 필요합니다.");
                }
                uriBuilder.path("/image/product").queryParam("name", itemName);
                break;

            case SHOP:
                if (!StringUtils.hasText(marketName) || !StringUtils.hasText(title) || !StringUtils.hasText(description)) {
                    throw new IllegalArgumentException("'marketName', 'title', 'description' 파라미터가 모두 필요합니다.");
                }
                uriBuilder.path("/image/shop")
                        .queryParam("marketName", marketName)
                        .queryParam("title", title)
                        .queryParam("description", description);
                break;

            default:
                throw new IllegalArgumentException("지원되지 않는 이미지 생성 도메인입니다: " + domain);
        }

        // 3. --- 여기서부터는 공통 로직 ---
        // 완성된 URI로 Flask 서버에 이미지 데이터 요청
        byte[] imageBytes = webClient.get()
                .uri(uriBuilder.build().toUri()) // 빌드된 URI 사용
                .retrieve()
                .onStatus(HttpStatusCode::isError, clientResponse ->
                        clientResponse.bodyToMono(String.class)
                                .flatMap(errorBody -> Mono.error(new AiServerException("AI 서버로부터 이미지를 가져오는데 실패했습니다. Error: " + errorBody, clientResponse.statusCode().value())))
                )
                .bodyToMono(byte[].class)
                .block();

        if (imageBytes == null || imageBytes.length == 0) {
            throw new AiServerException("AI 서버로부터 비어있는 이미지 데이터를 받았습니다.", 204);
        }

        // 4. 받아온 이미지 데이터를 파일로 변환하고 저장
        String contentType = "image/png"; // 간단하게 png로 가정
        String extension = "png";
        String filename = UUID.randomUUID().toString() + "." + extension;

        MultipartFile multipartFile = new ByteArrayMultipartFile(imageBytes, "file", filename, contentType);

        // 5. 최종 URL 반환
        return fileStorageService.storeFile(multipartFile);
    }
}