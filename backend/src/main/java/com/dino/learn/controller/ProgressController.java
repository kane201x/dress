package com.dino.learn.controller;

import com.dino.learn.dto.ApiResponse;
import com.dino.learn.dto.ProgressVO;
import com.dino.learn.entity.ModuleProgress;
import com.dino.learn.entity.Sticker;
import com.dino.learn.service.ProgressService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/progress")
public class ProgressController {

    private final ProgressService progressService;

    public ProgressController(ProgressService progressService) {
        this.progressService = progressService;
    }

    @PostMapping("/module")
    public ApiResponse<ModuleProgress> completeModule(@RequestBody Map<String, Object> body) {
        Long userId = Long.valueOf(body.get("userId").toString());
        String moduleKey = body.get("moduleKey").toString();
        ModuleProgress progress = progressService.completeModule(userId, moduleKey);
        return ApiResponse.success("Module completed", progress);
    }

    @GetMapping("/{userId}")
    public ApiResponse<ProgressVO> getProgress(@PathVariable Long userId) {
        ProgressVO progress = progressService.getProgress(userId);
        return ApiResponse.success(progress);
    }

    @PostMapping("/sticker")
    public ApiResponse<Sticker> unlockSticker(@RequestBody Map<String, Object> body) {
        Long userId = Long.valueOf(body.get("userId").toString());
        String stickerKey = body.get("stickerKey").toString();
        Sticker sticker = progressService.unlockSticker(userId, stickerKey);
        return ApiResponse.success("Sticker unlocked", sticker);
    }
}
