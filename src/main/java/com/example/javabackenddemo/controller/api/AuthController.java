package com.example.javabackenddemo.controller.api;

import com.example.javabackenddemo.dto.request.LoginRequest;
import com.example.javabackenddemo.dto.request.RegisterRequest;
import com.example.javabackenddemo.dto.response.ApiResponse;
import com.example.javabackenddemo.dto.response.LoginResponse;
import com.example.javabackenddemo.entity.User;
import com.example.javabackenddemo.mapper.UserMapper;
import com.example.javabackenddemo.security.JwtUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final UserMapper userMapper;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    public AuthController(UserMapper userMapper, JwtUtil jwtUtil, PasswordEncoder passwordEncoder) {
        this.userMapper = userMapper;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/register")
    public ApiResponse<LoginResponse> register(@Valid @RequestBody RegisterRequest request) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, request.username());
        if (userMapper.selectCount(wrapper) > 0) {
            return ApiResponse.error(HttpStatus.CONFLICT.value(), "Username already exists");
        }

        User user = User.builder()
                .username(request.username())
                .password(passwordEncoder.encode(request.password()))
                .email(request.email())
                .phone(request.phone())
                .build();
        userMapper.insert(user);

        String token = jwtUtil.generateToken(user.getId(), user.getUsername());
        return ApiResponse.success(new LoginResponse(token, user.getId(), user.getUsername(), user.getRole()));
    }

    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, request.username());
        User user = userMapper.selectOne(wrapper);

        if (user == null || !passwordEncoder.matches(request.password(), user.getPassword())) {
            return ApiResponse.error(HttpStatus.UNAUTHORIZED.value(), "Invalid username or password");
        }

        String token = jwtUtil.generateToken(user.getId(), user.getUsername());
        return ApiResponse.success(new LoginResponse(token, user.getId(), user.getUsername(), user.getRole()));
    }
}
