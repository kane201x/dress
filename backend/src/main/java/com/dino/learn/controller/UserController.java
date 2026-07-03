package com.dino.learn.controller;

import com.dino.learn.dto.ApiResponse;
import com.dino.learn.dto.UserVO;
import com.dino.learn.entity.User;
import com.dino.learn.service.UserService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{id}")
    public ApiResponse<UserVO> getUser(@PathVariable Long id) {
        User user = userService.getUser(id);
        return ApiResponse.success(userService.toVO(user));
    }

    @PutMapping("/{id}/difficulty")
    public ApiResponse<UserVO> updateDifficulty(@PathVariable Long id, @RequestBody Map<String, Integer> body) {
        int level = body.getOrDefault("difficulty", 1);
        UserVO userVO = userService.updateDifficulty(id, level);
        return ApiResponse.success(userVO);
    }

    @PutMapping("/{id}/stars")
    public ApiResponse<UserVO> updateStars(@PathVariable Long id, @RequestBody Map<String, Integer> body) {
        int delta = body.getOrDefault("delta", 0);
        UserVO userVO = userService.updateStars(id, delta);
        return ApiResponse.success(userVO);
    }
}
