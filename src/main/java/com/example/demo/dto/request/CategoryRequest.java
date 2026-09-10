package com.example.demo.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CategoryRequest {

    @NotBlank(message="Category name is required")
    @Size(max=100,message="Category name must not exceed 100 characters")
    private String name;
    @NotBlank(message = "Slug is required")
    @Size(max = 120, message = "Slug must not exceed 120 characters")
    private String slug;

    
    private Long parentId;

}