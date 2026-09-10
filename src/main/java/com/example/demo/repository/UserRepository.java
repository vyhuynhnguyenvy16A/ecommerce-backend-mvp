package com.example.demo.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    // Spring Data JPA sẽ tự động dịch tên hàm này thành câu lệnh SQL:
    // SELECT * FROM users WHERE email = ?
    Optional<User> findByEmail(String email);
    
    // (Tùy chọn) Hàm này cũng rất hay dùng khi đăng ký để kiểm tra email tồn tại chưa
    boolean existsByEmail(String email);
}