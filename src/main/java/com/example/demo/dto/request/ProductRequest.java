package com.example.demo.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ProductRequest {

    
    @NotNull(message = "Category ID is required")
    private Long categoryId;

    
    @NotBlank(message = "Product name is required")
    @Size(max = 200, message = "Product name must not exceed 200 characters")
    private String name;

    
    @NotBlank(message = "Slug is required")
    @Size(max = 220, message = "Slug must not exceed 220 characters")
    private String slug;

   
  
    private String description;

    
    @NotNull(message = "Base price is required")
    @Positive(message = "Base price must be greater than 0")
    private BigDecimal basePrice;

}