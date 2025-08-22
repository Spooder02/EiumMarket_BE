package com.eiummarket.demo.service;

import com.eiummarket.demo.dto.CategoryDto;
import com.eiummarket.demo.model.Category;
import com.eiummarket.demo.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryService {

    private final CategoryRepository categoryRepository;

    /**
     * 모든 카테고리 조회
     * @return 카테고리 응답 DTO 목록
     */
    public List<CategoryDto.Response> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }

    /**
     * 카테고리 추가
     * @param request 추가할 카테고리 정보 DTO
     * @return 생성된 카테고리 정보 DTO
     */
    @Transactional
    public CategoryDto.Response addCategory(CategoryDto.CreateRequest request) {
        if (categoryRepository.findByName(request.getName()).isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 카테고리 이름입니다: " + request.getName());
        }
        Category category = Category.builder()
                .name(request.getName())
                .build();
        Category savedCategory = categoryRepository.save(category);
        return convertToResponseDto(savedCategory);
    }

    /**
     * 카테고리 삭제
     * @param categoryId 삭제할 카테고리 ID
     */
    @Transactional
    public void deleteCategory(Long categoryId) {
        Category category = categoryRepository.findByCategoryId(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 카테고리 ID입니다: " + categoryId));
        categoryRepository.delete(category);
    }

    /**
     * Category 엔티티를 CategoryDto.Response DTO로 변환하는 헬퍼 메소드
     * @param category 변환할 Category 엔티티
     * @return 변환된 CategoryDto.Response
     */
    private CategoryDto.Response convertToResponseDto(Category category) {
        return CategoryDto.Response.builder()
                .categoryId(category.getCategoryId())
                .name(category.getName())
                .build();
    }
}
