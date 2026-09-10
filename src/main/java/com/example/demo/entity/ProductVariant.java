package com.example.demo.entity;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "product_variants")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductVariant {

    // TODO 8: Khóa chính
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // TODO 9: Quan hệ N-1 với Product
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    // TODO 10: Các thuộc tính phân loại
    @Column(nullable = false, unique = true) // Mã SKU của mỗi phiên bản phải là duy nhất
    private String sku;

    @Column(nullable = false)
    private String size;

    @Column(nullable = false)
    private String color;

    // TODO 11: Giá ghi đè (Nếu null thì lấy giá gốc của Product, nếu có thì dùng giá này)
    @Column(name = "price_override")
    private BigDecimal priceOverride;

    // TODO 12: Số lượng tồn kho
    @Column(name = "stock_quantity", nullable = false)
    private Integer stockQuantity;

    // TODO 13: Đánh dấu version để xử lý đụng độ dữ liệu (Concurrency)
    @Version
    @Column(nullable = false)
    private Integer version;
}