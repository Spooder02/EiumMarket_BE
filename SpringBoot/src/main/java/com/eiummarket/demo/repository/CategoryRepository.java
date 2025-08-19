package com.eiummarket.demo.repository;


import org.springframework.data.jpa.repository.JpaRepository;

import com.eiummarket.demo.model.Category;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    Optional<Category> findByName(String name);

    Optional<Category> findByCategoryId(Long categoryId);

    Long categoryId(Long categoryId);
}
