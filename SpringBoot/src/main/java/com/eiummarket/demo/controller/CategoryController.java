package com.eiummarket.demo.controller;

import com.eiummarket.demo.dto.CategoryDto;
import com.eiummarket.demo.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/categories")
@Tag(name = "Category API", description = "카테고리 관리 API")
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    @Operation(summary = "전체 카테고리 조회", description = "모든 카테고리 목록을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    public ResponseEntity<List<CategoryDto.Response>> getAllCategories() {
        return ResponseEntity.ok(categoryService.getAllCategories());
    }

    @PostMapping
    @Operation(summary = "카테고리 추가", description = "새로운 카테고리를 추가합니다.")
    @ApiResponse(responseCode = "201", description = "추가 성공")
    public ResponseEntity<CategoryDto.Response> addCategory(@Valid @RequestBody CategoryDto.CreateRequest request) {
        CategoryDto.Response newCategory = categoryService.addCategory(request);
        URI location = URI.create(String.format("/categories/%d", newCategory.getCategoryId()));
        return ResponseEntity.created(location).body(newCategory);
    }

    @DeleteMapping("/{categoryId}")
    @Operation(summary = "카테고리 삭제", description = "특정 카테고리를 삭제합니다.")
    @ApiResponse(responseCode = "204", description = "삭제 성공")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long categoryId) {
        categoryService.deleteCategory(categoryId);
        return ResponseEntity.noContent().build();
    }
}
