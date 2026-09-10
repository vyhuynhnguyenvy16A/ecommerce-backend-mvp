package com.example.demo.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.Cart;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {

    // TODO 1: Tìm Giỏ hàng dựa theo ID của người dùng
    Optional<Cart> findByUserId(Long userId);
}