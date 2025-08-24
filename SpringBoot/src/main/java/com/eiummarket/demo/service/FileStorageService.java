// src/main/java/com/eiummarket/demo/service/FileStorageService.java
package com.eiummarket.demo.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.*;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileStorageService {

    @Value("${app.upload.dir:static}")
    private String uploadDir;

    @Value("${app.upload.public-base:/static}")
    private String publicBase;

    public String storeFile(MultipartFile file) {
        if (file == null || file.isEmpty()) return null;
        try {
            Path dir = Paths.get(uploadDir).toAbsolutePath().normalize();
            Files.createDirectories(dir);

            String ext = StringUtils.getFilenameExtension(file.getOriginalFilename());
            String filename = UUID.randomUUID() + (ext == null ? "" : "." + ext);
            Path target = dir.resolve(filename);

            try (InputStream in = file.getInputStream()) {
                Files.copy(in, target, StandardCopyOption.REPLACE_EXISTING);
            }
            // 공개 URL로 리턴
            return publicBase.endsWith("/")
                    ? publicBase + filename
                    : publicBase + "/" + filename;
        } catch (IOException e) {
            throw new RuntimeException("파일 저장 실패", e);
        }
    }
}
