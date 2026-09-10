package com.example.demo.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RefreshTokenRequest {
    
    // Đảm bảo Frontend không được gửi lên một chuỗi rỗng hoặc null
    @NotBlank(message = "Refresh token cannot be blank")
    private String refreshToken;
    
}