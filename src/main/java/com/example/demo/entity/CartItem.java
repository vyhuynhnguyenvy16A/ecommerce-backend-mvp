package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "cart_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartItem {

    // TODO 4: Khóa chính của CartItem
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // TODO 5: Quan hệ N-1 với Cart. Nhiều món hàng nằm trong 1 giỏ.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id", nullable = false)
    private Cart cart;

    // TODO 6: Quan hệ N-1 với ProductVariant (hoặc Product)
    // 💡 Lưu ý: Trong bản MVP tối giản, nếu em chưa tạo class ProductVariant, 
    // em hoàn toàn có thể đổi kiểu dữ liệu dưới đây thành `Product` và 
    // đổi tên cột thành `product_id` cho dễ chạy nhé.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_variant_id", nullable = false)
    private ProductVariant productVariant;

    // TODO 7: Số lượng sản phẩm (bắt buộc phải có)
    @Column(nullable = false)
    private Integer quantity;
}