package com.dino.learn.controller;

import com.dino.learn.config.JwtConfig;
import com.dino.learn.dto.ApiResponse;
import com.dino.learn.dto.LoginRequest;
import com.dino.learn.dto.UserVO;
import com.dino.learn.entity.User;
import com.dino.learn.service.UserService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final JwtConfig jwtConfig;

    public AuthController(UserService userService, JwtConfig jwtConfig) {
        this.userService = userService;
        this.jwtConfig = jwtConfig;
    }

    @PostMapping("/login")
    public ApiResponse<Map<String, Object>> login(@Valid @RequestBody LoginRequest request) {
        User user = userService.createUser(request.getName());
        String token = jwtConfig.generateToken(user.getId(), user.getName());
        UserVO userVO = userService.toVO(user);
        Map<String, Object> data = Map.of("user", userVO, "token", token);
        return ApiResponse.success("Login successful", data);
    }
}
