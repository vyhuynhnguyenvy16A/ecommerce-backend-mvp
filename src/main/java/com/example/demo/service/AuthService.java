package com.example.demo.service;

import com.example.demo.dto.request.LoginRequest;
import com.example.demo.dto.request.RegisterRequest;
import com.example.demo.dto.response.AuthResponse;
import com.example.demo.dto.response.UserResponse;
import com.example.demo.entity.User;
import com.example.demo.exception.EmailAlreadyExistsException;
import com.example.demo.exception.InvalidCredentialsException;
import com.example.demo.mapper.UserMapper;
import com.example.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final UserMapper userMapper;
    private final RedisTemplate<String, String> redisTemplate;

    @Value("${jwt.access-token-expiration}")
    private long accessTokenExpiration;

    @Value("${jwt.refresh-token-expiration}")
    private long refreshTokenExpiration;

    public UserResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyExistsException("Email already registered: " + request.getEmail());
        }

        User user = User.builder()
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getFullName())
                .phone(request.getPhone())
                .role("CUSTOMER")
                .build();

        User saved = userRepository.save(user);
        return userMapper.toResponse(saved);
    }

    public AuthResponse login(LoginRequest request, String deviceId) {
        User user = userRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> new InvalidCredentialsException("Invalid email or password"));
        if(!passwordEncoder.matches(request.getPassword(),user.getPasswordHash())){
            throw new InvalidCredentialsException("Invalid email or password");
        }
        String accessToken = jwtService.generateAccessToken(user.getId(),user.getRole());
        String refreshToken = jwtService.generateRefreshToken();

        String redisKey = "refresh_token:" + refreshToken;
        String redisValue = user.getId() + ":" + deviceId; 
        redisTemplate.opsForValue().set(
            redisKey,
            redisValue,
            Duration.ofMillis(refreshTokenExpiration)
        );
        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .expiresIn(accessTokenExpiration/1000)
                .tokenType("Bearer")
                .build();
                
    }
    public AuthResponse refreshToken(String refreshToken) {
        String redisKey = "refresh_token:" + refreshToken;
        String storedValue = redisTemplate.opsForValue().get(redisKey);
        if (storedValue==null){
            throw new InvalidCredentialsException("Invalid email or password");
        }
        Long userId = Long.parseLong(storedValue.split(":")[0]);
        User user = userRepository.findById(userId)
                    .orElseThrow(() -> new InvalidCredentialsException("User not found in system"));
        String newAccessToken = jwtService.generateAccessToken(user.getId(), user.getRole());
        return AuthResponse.builder()
            .accessToken(newAccessToken)
            .refreshToken(refreshToken) // Trả lại token cũ
            .expiresIn(accessTokenExpiration / 1000)
            .tokenType("Bearer")
            .build();
    }
    public void logout(String refreshToken) {
    if (refreshToken != null) {
        String redisKey = "refresh_token:" + refreshToken;
        redisTemplate.delete(redisKey);
    }
}
}
