package com.eiummarket.demo.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.unit.DataSize;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Value("${ai.flask.server.url}")
    private String flaskServerUrl;

    @Value("${webclient.max-buffer-size}")
    private String maxBufferSize;

    @Bean
    public WebClient webClient(WebClient.Builder builder) {

        int sizeInBytes = (int) DataSize.parse(maxBufferSize).toBytes();

        // 이미지 때문에 메모리 버퍼 10MB로 설정
        ExchangeStrategies exchangeStrategies = ExchangeStrategies.builder()
                .codecs(configurer -> configurer
                        .defaultCodecs()
                        .maxInMemorySize(sizeInBytes))
                .build();

        return builder
                .baseUrl(flaskServerUrl)
                .exchangeStrategies(exchangeStrategies)
                .build();
    }
}