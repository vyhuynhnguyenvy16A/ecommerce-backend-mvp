package com.example.demo.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddToCartRequest {

    // TODO 3: ID của phiên bản sản phẩm khách muốn mua
    @NotNull(message = "Variant ID is required")
    private Long variantId;

    // TODO 4: Số lượng mua phải là số dương (> 0)
    @NotNull(message = "Quantity is required")
    @Positive(message = "Quantity must be greater than 0")
    private Integer quantity;
    
}