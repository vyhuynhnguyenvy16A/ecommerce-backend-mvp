package com.example.demo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>,JpaSpecificationExecutor<Product> {

    // TODO 22: method existsBySlug(String slug) -> boolean
    boolean existsBySlug(String slug);

    // TODO 23: method findBySlug(String slug) -> Optional<Product>
    Optional<Product> findBySlug(String slug);

    // TODO 24: method tìm Product theo category
    List<Product> findByCategoryId(Long categoryId);

}