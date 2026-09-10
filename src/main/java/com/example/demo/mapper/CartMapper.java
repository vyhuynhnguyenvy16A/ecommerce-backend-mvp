package com.example.demo.mapper;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Component;

import com.example.demo.dto.response.CartItemResponse;
import com.example.demo.dto.response.CartResponse;
import com.example.demo.entity.Cart;
import com.example.demo.entity.CartItem;
import com.example.demo.entity.Product;
import com.example.demo.entity.ProductVariant;

@Component
public class CartMapper {

    public CartItemResponse toItemResponse(CartItem item) {
        ProductVariant variant = item.getProductVariant();
        Product product = variant.getProduct();

        // TODO 8: Xác định "giá thực tế áp dụng" (unitPrice)
        // Ưu tiên 1: Giá ghi đè (khuyến mãi, xả kho...) của riêng phiên bản (Size/Color) này
        // Ưu tiên 2: Nếu không có giá ghi đè (null), lấy giá niêm yết của sản phẩm gốc
        BigDecimal unitPrice = variant.getPriceOverride() != null 
                ? variant.getPriceOverride() 
                : product.getBasePrice();

        // Tính thành tiền của món hàng này (subtotal)
        BigDecimal subtotal = unitPrice.multiply(BigDecimal.valueOf(item.getQuantity()));

        return CartItemResponse.builder()
                .id(item.getId())
                .variantId(variant.getId())
                .productName(product.getName())
                .sku(variant.getSku())
                .size(variant.getSize())
                .color(variant.getColor())
                .unitPrice(unitPrice)
                .quantity(item.getQuantity())
                .subtotal(subtotal)
                .build();
    }

    public CartResponse toResponse(Cart cart) {
        if (cart == null) {
            return null;
        }

        // TODO 9: Lặp qua từng món hàng (CartItem) và biến đổi nó thành DTO (CartItemResponse)
        List<CartItemResponse> itemResponses = cart.getItems().stream()
                .map(this::toItemResponse)
                .toList();

        // Tính tổng tiền giỏ hàng bằng cách cộng dồn tất cả các subtotal
        BigDecimal totalAmount = itemResponses.stream()
                .map(CartItemResponse::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return CartResponse.builder()
                .id(cart.getId())
                .items(itemResponses)
                .totalAmount(totalAmount)
                .build();
    }
}