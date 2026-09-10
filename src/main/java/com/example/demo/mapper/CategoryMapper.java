package com.example.demo.mapper;

import com.example.demo.dto.response.CategoryResponse;
import com.example.demo.entity.Category;
import org.springframework.stereotype.Component;

@Component
public class CategoryMapper {

    public CategoryResponse toResponse(Category category) {
        Long parentId = (category.getParent() != null)
                ? category.getParent().getId()
                : null;

        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .slug(category.getSlug())
                .parentId(parentId)
                .build();
    }
}