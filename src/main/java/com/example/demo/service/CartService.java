package com.example.demo.service;

import java.util.ArrayList;
import java.util.Optional;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.dto.request.AddToCartRequest;
import com.example.demo.dto.request.UpdateCartItemRequest;
import com.example.demo.dto.response.CartResponse;
import com.example.demo.entity.Cart;
import com.example.demo.entity.CartItem;
import com.example.demo.entity.ProductVariant;
import com.example.demo.entity.User;
import com.example.demo.exception.InsufficientStockException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.mapper.CartMapper;
import com.example.demo.repository.CartItemRepository;
import com.example.demo.repository.CartRepository;
import com.example.demo.repository.ProductVariantRepository;
import com.example.demo.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductVariantRepository productVariantRepository;
    private final UserRepository userRepository; // Bổ sung để tìm User khi tạo giỏ mới
    private final CartMapper cartMapper;

    // --- HÀM DÙNG CHUNG (Tách ra để tái sử dụng) ---
    private Cart getOrCreateCart(Long userId) {
        return cartRepository.findByUserId(userId)
                .orElseGet(() -> {
                    User user = userRepository.findById(userId)
                            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
                    
                    Cart newCart = Cart.builder()
                            .user(user)
                            .items(new ArrayList<>())
                            .build();
                    return cartRepository.save(newCart);
                });
    }

    public CartResponse getCart(Long userId) {
        // TODO 10 & 11: Lấy giỏ hàng, nếu chưa có thì tự động tạo mới (Lazy-create)
        Cart cart = getOrCreateCart(userId);
        return cartMapper.toResponse(cart);
    }

    @Transactional
    public CartResponse addItem(Long userId, AddToCartRequest request) {
        Cart cart = getOrCreateCart(userId);

        // TODO 12: Tìm ProductVariant
        ProductVariant variant = productVariantRepository.findById(request.getVariantId())
                .orElseThrow(() -> new ResourceNotFoundException("Variant not found"));

        // TODO 13: Kiểm tra tồn kho trước khi thêm
        if (variant.getStockQuantity() < request.getQuantity()) {
            throw new InsufficientStockException("Not enough stock for variant: " + variant.getSku());
        }

        // TODO 14: Kiểm tra xem sản phẩm đã có trong giỏ chưa
        Optional<CartItem> existingItem = cartItemRepository.findByCartIdAndProductVariantId(cart.getId(), variant.getId());

        if (existingItem.isPresent()) {
            // Nếu ĐÃ CÓ: Cộng dồn số lượng
            CartItem item = existingItem.get();
            int newQuantity = item.getQuantity() + request.getQuantity();
            
            // Phải kiểm tra lại tồn kho với tổng số lượng mới
            if (variant.getStockQuantity() < newQuantity) {
                throw new InsufficientStockException("Not enough stock for accumulated quantity");
            }
            item.setQuantity(newQuantity);
            cartItemRepository.save(item);
        } else {
            // Nếu CHƯA CÓ: Tạo mới
            CartItem newItem = CartItem.builder()
                    .cart(cart)
                    .productVariant(variant)
                    .quantity(request.getQuantity())
                    .build();
            cartItemRepository.save(newItem);
        }

        // TODO 15: Tải lại giỏ hàng từ Database để Mapper lấy được dữ liệu mới nhất
        Cart updatedCart = cartRepository.findById(cart.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found"));
        return cartMapper.toResponse(updatedCart);
    }

    @Transactional
    public CartResponse updateItemQuantity(Long userId, Long itemId, UpdateCartItemRequest request) {
        // TODO 16: Tìm CartItem
        CartItem item = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("CartItem not found"));

        // TODO 17: BẢO MẬT CHỐNG IDOR - Phải đảm bảo item này thuộc về giỏ của user đang gọi API
        if (!item.getCart().getUser().getId().equals(userId)) {
            throw new AccessDeniedException("You do not have permission to modify this cart item");
        }

        // TODO 18: Kiểm tra tồn kho cho số lượng mới
        if (item.getProductVariant().getStockQuantity() < request.getQuantity()) {
            throw new InsufficientStockException("Not enough stock");
        }

        // TODO 19: Cập nhật và lưu lại
        item.setQuantity(request.getQuantity());
        cartItemRepository.save(item);

        Cart updatedCart = cartRepository.findById(item.getCart().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found"));
        return cartMapper.toResponse(updatedCart);
    }

    @Transactional
    public void removeItem(Long userId, Long itemId) {
        // TODO 20: Tìm item và kiểm tra IDOR
        CartItem item = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("CartItem not found"));

        if (!item.getCart().getUser().getId().equals(userId)) {
            throw new AccessDeniedException("You do not have permission to modify this cart item");
        }

        // TODO 21: Xóa item
        cartItemRepository.delete(item);
    }
}