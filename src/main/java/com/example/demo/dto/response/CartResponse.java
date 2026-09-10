package com.example.demo.dto.response;

import java.math.BigDecimal;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartResponse {

    // TODO 7: Thông tin tổng quan của Giỏ hàng
    private Long id;                          // ID của Giỏ hàng
    private List<CartItemResponse> items;     // Danh sách các món hàng (đã được trải phẳng dữ liệu)
    private BigDecimal totalAmount;           // Tổng số tiền khách phải trả (cộng dồn từ các subtotal)
    
}