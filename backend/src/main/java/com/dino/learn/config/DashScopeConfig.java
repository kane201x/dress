package com.dino.learn.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DashScopeConfig {

    @Value("${ai.dashscope.direct-api-key}")
    private String directApiKey;

    @Value("${ai.dashscope.base-url}")
    private String baseUrl;

    @Value("${ai.dashscope.model}")
    private String model;

    @Value("${ai.dashscope.vl-model}")
    private String vlModel;

    @Value("${ai.dashscope.tts-model}")
    private String ttsModel;

    @Value("${ai.dashscope.tts-voice}")
    private String ttsVoice;

    @Value("${ai.dashscope.api-key}")
    private String apiKey;

    public String getDirectApiKey() { return directApiKey; }
    public String getBaseUrl() { return baseUrl; }
    public String getModel() { return model; }
    public String getVlModel() { return vlModel; }
    public String getTtsModel() { return ttsModel; }
    public String getTtsVoice() { return ttsVoice; }
    public String getApiKey() { return apiKey; }
}
