package com.example.demo.service;

import com.example.demo.dto.request.CategoryRequest;
import com.example.demo.dto.response.CategoryResponse;
import com.example.demo.entity.Category;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.exception.SlugAlreadyExistsException;
import com.example.demo.mapper.CategoryMapper;
import com.example.demo.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    public CategoryResponse create(CategoryRequest request) {
        // TODO 5: Kiểm tra trùng lặp slug
        if (categoryRepository.existsBySlug(request.getSlug())) {
            throw new SlugAlreadyExistsException("Slug already exists: " + request.getSlug());
        }

        // TODO 6: Xử lý tìm Category cha
        Category parent = null;
        if (request.getParentId() != null) {
            parent = categoryRepository.findById(request.getParentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Parent category not found"));
        }

        // TODO 7: Xây dựng Category mới
        Category category = Category.builder()
                .name(request.getName())
                .slug(request.getSlug())
                .parent(parent)
                .build();

        // TODO 8: Lưu xuống DB và chuyển đổi sang DTO trả về
        Category saved = categoryRepository.save(category);
        return categoryMapper.toResponse(saved);
    }

    public CategoryResponse update(Long id, CategoryRequest request) {
        // TODO 9: Kiểm tra danh mục có tồn tại không
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

        // TODO 10: Xử lý cập nhật thông tin và kiểm tra trùng Slug
        // Chỉ check DB nếu người dùng thực sự muốn đổi sang một slug hoàn toàn mới
        if (!category.getSlug().equals(request.getSlug())) {
            if (categoryRepository.existsBySlug(request.getSlug())) {
                throw new SlugAlreadyExistsException("Slug already exists: " + request.getSlug());
            }
            category.setSlug(request.getSlug());
        }
        
        category.setName(request.getName());

        // Cập nhật lại cha (nếu có sự thay đổi)
        if (request.getParentId() != null) {
            Category parent = categoryRepository.findById(request.getParentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Parent category not found"));
            category.setParent(parent);
        } else {
            category.setParent(null); // Gỡ danh mục cha nếu request gửi lên null
        }

        // TODO 11: Lưu và trả về
        Category saved = categoryRepository.save(category);
        return categoryMapper.toResponse(saved);
    }

    public void delete(Long id) {
        // TODO 12: Tìm và xóa cứng danh mục
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
                
        categoryRepository.delete(category);
    }
}