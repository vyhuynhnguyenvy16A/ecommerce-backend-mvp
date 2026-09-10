package com.example.demo.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.request.AddToCartRequest;
import com.example.demo.dto.request.UpdateCartItemRequest;
import com.example.demo.dto.response.CartResponse;
import com.example.demo.service.CartService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @GetMapping
    public ResponseEntity<CartResponse> getCart(Authentication authentication) {
        // TODO 23: Lấy userId từ Token
        Long userId = Long.parseLong(authentication.getName());
        
        // Gọi Service và trả về
        CartResponse response = cartService.getCart(userId);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<CartResponse> addItem(
            Authentication authentication, 
            @Valid @RequestBody AddToCartRequest request) {
        
        // TODO 24: Lấy userId và thêm vào giỏ
        Long userId = Long.parseLong(authentication.getName());
        CartResponse response = cartService.addItem(userId, request);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{itemId}")
    public ResponseEntity<CartResponse> updateItem(
            Authentication authentication,
            @PathVariable Long itemId,
            @Valid @RequestBody UpdateCartItemRequest request) {
        
        // TODO 25: Lấy userId và cập nhật số lượng
        Long userId = Long.parseLong(authentication.getName());
        CartResponse response = cartService.updateItemQuantity(userId, itemId, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{itemId}")
    public ResponseEntity<Void> removeItem(
            Authentication authentication, 
            @PathVariable Long itemId) {
        
        // TODO 26: Lấy userId và xóa món hàng
        Long userId = Long.parseLong(authentication.getName());
        cartService.removeItem(userId, itemId);
        
        // Trả về mã 204 NO_CONTENT (Thành công nhưng không có body trả về)
        return ResponseEntity.noContent().build();
    }
}