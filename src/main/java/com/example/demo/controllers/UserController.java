package com.example.demo.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.response.UserResponse;
import com.example.demo.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getMe(Authentication authentication) {
        // TODO 3: Lấy ID của user từ token (đã được lưu trong SecurityContext)
        Long userId = Long.parseLong(authentication.getName());

        // TODO 4: Gọi Service để lấy thông tin hồ sơ và trả về
        UserResponse response = userService.getCurrentUser(userId);
        return ResponseEntity.ok(response);
    }
}