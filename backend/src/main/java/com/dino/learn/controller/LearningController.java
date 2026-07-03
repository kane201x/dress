package com.dino.learn.controller;

import com.dino.learn.dto.ApiResponse;
import com.dino.learn.entity.poem.Poem;
import com.dino.learn.entity.vocab.VocabItem;
import com.dino.learn.service.LearningDataService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/learn")
public class LearningController {

    private final LearningDataService learningDataService;

    public LearningController(LearningDataService learningDataService) {
        this.learningDataService = learningDataService;
    }

    @GetMapping("/poems")
    public ApiResponse<List<Poem>> getPoems(@RequestParam(required = false) String category) {
        List<Poem> poems = learningDataService.getPoems(category);
        return ApiResponse.success(poems);
    }

    @GetMapping("/poems/{id}")
    public ApiResponse<Poem> getPoem(@PathVariable Long id) {
        Poem poem = learningDataService.getPoem(id)
                .orElseThrow(() -> new RuntimeException("Poem not found: " + id));
        return ApiResponse.success(poem);
    }

    @GetMapping("/alphabet")
    public ApiResponse<List<Map<String, Object>>> getAlphabet() {
        return ApiResponse.success(learningDataService.getAlphabet());
    }

    @GetMapping("/colors")
    public ApiResponse<List<Map<String, Object>>> getColors() {
        return ApiResponse.success(learningDataService.getColors());
    }

    @GetMapping("/shapes")
    public ApiResponse<List<Map<String, Object>>> getShapes() {
        return ApiResponse.success(learningDataService.getShapes());
    }

    @GetMapping("/compare-pairs")
    public ApiResponse<List<Map<String, Object>>> getComparePairs() {
        return ApiResponse.success(learningDataService.getComparePairs());
    }

    @GetMapping("/vocab/{category}")
    public ApiResponse<List<VocabItem>> getVocabItems(@PathVariable String category) {
        List<VocabItem> items = learningDataService.getVocabItems(category);
        return ApiResponse.success(items);
    }
}
