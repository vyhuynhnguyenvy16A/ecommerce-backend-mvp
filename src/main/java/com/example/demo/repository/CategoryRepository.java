package com.example.demo.repository;

import com.example.demo.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


public interface CategoryRepository extends JpaRepository<Category, Long> {

    // TODO 20: method existsBySlug(String slug) -> trả về boolean
    // Dùng để kiểm tra nhanh xem đường dẫn (slug) đã bị ai xí chỗ chưa trước khi lưu.
    boolean existsBySlug(String slug);

    // TODO 21: method findBySlug(String slug) -> trả về Optional<Category>
    // Dùng để tìm kiếm chính xác một danh mục dựa trên đường dẫn.
    Optional<Category> findBySlug(String slug);

}