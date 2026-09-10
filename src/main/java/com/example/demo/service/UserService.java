package com.example.demo.service;

import org.springframework.stereotype.Service;

import com.example.demo.dto.response.UserResponse;
import com.example.demo.entity.User;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.mapper.UserMapper;
import com.example.demo.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserResponse getCurrentUser(Long userId) {
        // TODO 1: Tìm User trong Database, nếu không thấy thì ném lỗi
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // TODO 2: Chuyển đổi từ Entity (chứa dữ liệu nhạy cảm) sang DTO (an toàn) và trả về
        return userMapper.toResponse(user);
    }
}