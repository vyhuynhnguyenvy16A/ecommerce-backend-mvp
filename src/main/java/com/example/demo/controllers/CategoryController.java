package com.example.demo.controllers;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.response.CategoryResponse;
import com.example.demo.mapper.CategoryMapper;
import com.example.demo.repository.CategoryRepository;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @GetMapping
    public List<CategoryResponse> getAll() {
        // TODO 29: lấy toàn bộ category (categoryRepository.findAll()),
        // map từng cái sang CategoryResponse bằng categoryMapper, return list
        // Gợi ý: dùng .stream().map(...).toList()
        return null;
    }
}