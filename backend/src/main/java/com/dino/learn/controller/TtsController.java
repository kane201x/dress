package com.dino.learn.controller;

import com.dino.learn.service.TtsService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/tts")
public class TtsController {

    private final TtsService ttsService;

    public TtsController(TtsService ttsService) {
        this.ttsService = ttsService;
    }

    @PostMapping(value = "/speak", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public @ResponseBody byte[] speak(@RequestBody Map<String, String> request) {
        String text = request.get("text");
        String language = request.getOrDefault("language", "English");
        return ttsService.synthesizeSpeech(text, language);
    }
}
