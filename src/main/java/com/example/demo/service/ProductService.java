package com.example.demo.service;

import com.example.demo.dto.request.ProductRequest;
import com.example.demo.dto.response.ProductResponse;
import com.example.demo.entity.Category;
import com.example.demo.entity.Product;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.exception.SlugAlreadyExistsException;
import com.example.demo.mapper.ProductMapper;
import com.example.demo.repository.CategoryRepository;
import com.example.demo.repository.ProductRepository;
import com.example.demo.specification.ProductSpecification;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import java.math.BigDecimal;
@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductMapper productMapper;

    public ProductResponse create(ProductRequest request) {
        // TODO 13: Kiểm tra slug trùng lặp
        if (productRepository.existsBySlug(request.getSlug())) {
            throw new SlugAlreadyExistsException("Product slug already exists: " + request.getSlug());
        }

        // TODO 14: Tìm Category theo ID, đảm bảo danh mục phải tồn tại trước khi gán cho sản phẩm
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + request.getCategoryId()));

        // TODO 15: Build Product mới. Không cần set "status" vì @Builder.Default đã lo việc đó.
        Product product = Product.builder()
                .name(request.getName())
                .slug(request.getSlug())
                .description(request.getDescription())
                .basePrice(request.getBasePrice())
                .category(category)
                .build();

        // TODO 16: Lưu xuống DB và chuyển sang DTO trả về
        Product savedProduct = productRepository.save(product);
        return productMapper.toResponse(savedProduct);
    }

    public ProductResponse update(Long id, ProductRequest request) {
        // TODO 17: Tìm Product theo id
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));

        // TODO 18: Kiểm tra và cập nhật Category nếu có sự thay đổi
        if (!product.getCategory().getId().equals(request.getCategoryId())) {
            Category newCategory = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("New Category not found with id: " + request.getCategoryId()));
            product.setCategory(newCategory);
        }

        // TODO 19: Xử lý cập nhật thông tin và kiểm tra trùng Slug (loại trừ chính nó)
        if (!product.getSlug().equals(request.getSlug())) {
            if (productRepository.existsBySlug(request.getSlug())) {
                throw new SlugAlreadyExistsException("Product slug already exists: " + request.getSlug());
            }
            product.setSlug(request.getSlug());
        }

        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setBasePrice(request.getBasePrice());

        // TODO 20: Lưu lại và map sang response
        Product updatedProduct = productRepository.save(product);
        return productMapper.toResponse(updatedProduct);
    }

    public void softDelete(Long id) {
        // TODO 21: Tìm product theo id
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));

        // TODO 22: Soft-delete bằng cách chuyển trạng thái
        product.setStatus("DELETED");
        productRepository.save(product);
    }

    public ProductResponse getById(Long id) {
        // TODO 23: Lấy chi tiết sản phẩm
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
        return productMapper.toResponse(product);
    }
    public Page<ProductResponse> search(
        Long categoryId,
        BigDecimal minPrice,
        BigDecimal maxPrice,
        int page,
        int size,
        String sortBy,
        String sortDirection

    ){
        Sort.Direction direction = sortDirection.equalsIgnoreCase("desc")? Sort.Direction.DESC : Sort.Direction.ASC;
        Sort sort = Sort.by(direction,sortBy);
        Pageable pageable = PageRequest.of(page,size,sort);
        Page<Product> productPage = productRepository.findAll(
                ProductSpecification.filterBy(categoryId, minPrice, maxPrice, "ACTIVE"),
                pageable
        );

        // TODO 9: Chuyển đổi dữ liệu từ Entity sang DTO
        return productPage.map(productMapper::toResponse);

    }

}