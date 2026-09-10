package com.example.demo.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.CartItem;

@Repository // Đánh dấu để Spring Boot biết đây là một bean Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    
    // Spring Data JPA sẽ tự động hiểu:
    // Tìm CartItem có Cart.id = cartId VÀ ProductVariant.id = variantId
    Optional<CartItem> findByCartIdAndProductVariantId(Long cartId, Long variantId);
    
}