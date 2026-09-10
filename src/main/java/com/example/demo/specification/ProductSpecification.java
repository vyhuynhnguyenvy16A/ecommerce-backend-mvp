package com.example.demo.specification;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import com.example.demo.entity.Product;

import jakarta.persistence.criteria.Predicate;

public class ProductSpecification {

    public static Specification<Product> filterBy(
            Long categoryId,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            String status
    ) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // TODO 1: Lọc theo danh mục (category)
            if (categoryId != null) {
                predicates.add(criteriaBuilder.equal(root.get("category").get("id"), categoryId));
            }

            // TODO 2: Lọc theo mức giá tối thiểu
            if (minPrice != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("basePrice"), minPrice));
            }

            // TODO 3: Lọc theo mức giá tối đa
            if (maxPrice != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("basePrice"), maxPrice));
            }

            // TODO 4: Lọc theo trạng thái (chặn hiển thị sản phẩm DELETED)
            if (status != null) {
                predicates.add(criteriaBuilder.equal(root.get("status"), status));
            }

            // TODO 5: Gộp tất cả các điều kiện lại bằng toán tử AND
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}