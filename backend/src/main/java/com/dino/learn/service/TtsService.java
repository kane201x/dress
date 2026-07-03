package com.dino.learn.service;

import com.dino.learn.config.DashScopeConfig;
import com.dino.learn.entity.AudioRecord;
import com.dino.learn.repository.AudioRecordRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HexFormat;
import java.util.Map;
import java.util.Optional;

@Service
public class TtsService {

    private static final Logger log = LoggerFactory.getLogger(TtsService.class);

    private final DashScopeConfig dashScopeConfig;
    private final MinioService minioService;
    private final AudioRecordRepository audioRecordRepository;
    private final RestTemplate restTemplate;

    public TtsService(DashScopeConfig dashScopeConfig, MinioService minioService, AudioRecordRepository audioRecordRepository) {
        this.dashScopeConfig = dashScopeConfig;
        this.minioService = minioService;
        this.audioRecordRepository = audioRecordRepository;
        this.restTemplate = new RestTemplate();
    }

    public byte[] synthesizeSpeech(String text, String language) {
        String fileKey = buildFileKey(text, language);

        Optional<AudioRecord> existing = audioRecordRepository.findByFileKey(fileKey);
        if (existing.isPresent()) {
            byte[] cached = minioService.download(fileKey);
            if (cached != null) {
                log.debug("TTS cache hit: {}", fileKey);
                return cached;
            }
        }

        log.debug("TTS cache miss, generating: {}", fileKey);
        byte[] audio = callTtsApi(text, language);
        if (audio.length > 0) {
            minioService.upload(fileKey, audio, "audio/mpeg");
            audioRecordRepository.save(new AudioRecord(fileKey, text, language, (long) audio.length));
        }
        return audio;
    }

    private byte[] callTtsApi(String text, String language) {
        try {
            String url = dashScopeConfig.getBaseUrl() + "/services/aigc/text-generation/generation";
            String languageType = "Chinese".equalsIgnoreCase(language) || "zh".equalsIgnoreCase(language) ? "Chinese" : "English";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(dashScopeConfig.getDirectApiKey());

            Map<String, Object> input = Map.of("text", text);
            Map<String, Object> parameters = Map.of(
                "tts", true,
                "language", languageType,
                "voice", dashScopeConfig.getTtsVoice()
            );
            Map<String, Object> requestBody = Map.of(
                "model", dashScopeConfig.getTtsModel(),
                "input", input,
                "parameters", parameters
            );

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST, entity, Map.class);

            if (response.getBody() != null) {
                Map<String, Object> output = (Map<String, Object>) response.getBody().get("output");
                if (output != null && output.get("audio") != null) {
                    return java.util.Base64.getDecoder().decode((String) output.get("audio"));
                }
            }

            log.warn("No audio in TTS response");
            return new byte[0];
        } catch (Exception e) {
            log.error("TTS API call failed: {}", e.getMessage());
            return new byte[0];
        }
    }

    static String buildFileKey(String text, String language) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] hash = md.digest((text + "|" + language).getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash) + ".mp3";
        } catch (Exception e) {
            return String.valueOf(text.hashCode()) + ".mp3";
        }
    }
}
