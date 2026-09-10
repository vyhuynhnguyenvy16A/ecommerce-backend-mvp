package com.example.demo.controllers;

import com.example.demo.dto.request.LoginRequest;
import com.example.demo.dto.request.RefreshTokenRequest;
import com.example.demo.dto.request.RegisterRequest;
import com.example.demo.dto.response.AuthResponse;
import com.example.demo.dto.response.UserResponse;
import com.example.demo.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@Valid @RequestBody RegisterRequest request) {
        UserResponse response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @Valid @RequestBody LoginRequest request,
            @RequestHeader(value = "X-Device-Id", required = false) String deviceId) {
        String resolvedDeviceId = (deviceId != null) ? deviceId : "default";
        AuthResponse response = authService.login(request, resolvedDeviceId);
        return ResponseEntity.ok(response);
    }
    // (Đảm bảo em đã import RefreshTokenRequest và AuthResponse ở đầu file)

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        // Lấy token từ hộp Request, đưa xuống tầng Service xử lý
        AuthResponse response = authService.refreshToken(request.getRefreshToken());
        
        // Trả về mã 200 OK cùng với gói Token mới
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@Valid @RequestBody RefreshTokenRequest request) {
        // Báo cho Service biết để xóa token này khỏi kho Redis
        authService.logout(request.getRefreshToken());
        
        // Trả về mã 204 No Content báo hiệu thành công
        return ResponseEntity.noContent().build();
    }
}
