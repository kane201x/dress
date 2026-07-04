package com.dino.learn.controller;

import com.dino.learn.service.TtsService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/tts")
public class TtsController {

    private final TtsService ttsService;

    public TtsController(TtsService ttsService) {
        this.ttsService = ttsService;
    }

    @PostMapping(value = "/speak", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<byte[]> speak(@RequestBody Map<String, String> request) {
        String text = request.get("text");
        String language = request.getOrDefault("language", "English");
        byte[] audio = ttsService.synthesizeSpeech(text, language);
        if (audio == null || audio.length == 0) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("audio/mpeg"))
                .body(audio);
    }

    @GetMapping(value = "/speak")
    public ResponseEntity<byte[]> speakGet(
            @RequestParam String text,
            @RequestParam(defaultValue = "Chinese") String language) {
        byte[] audio = ttsService.synthesizeSpeech(text, language);
        if (audio == null || audio.length == 0) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("audio/mpeg"))
                .body(audio);
    }
}
