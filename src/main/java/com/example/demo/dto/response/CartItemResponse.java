package com.example.demo.dto.response;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartItemResponse {

    // TODO 6: Các trường dữ liệu cần thiết để Frontend hiển thị giỏ hàng
    private Long id;              // ID của dòng giỏ hàng (CartItem)
    private Long variantId;       // ID của phiên bản sản phẩm
    private String productName;   // Tên sản phẩm chính
    private String sku;           // Mã vạch / SKU
    private String size;          // Kích cỡ
    private String color;         // Màu sắc
    private BigDecimal unitPrice; // Đơn giá (đã tính toán giá khuyến mãi nếu có)
    private Integer quantity;     // Số lượng khách mua
    private BigDecimal subtotal;  // Thành tiền của món này (unitPrice * quantity)
    
}