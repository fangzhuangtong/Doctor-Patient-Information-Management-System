package com.example.hello.service;

import com.example.hello.config.DeepSeekConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import okhttp3.*;
import okio.BufferedSource;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
public class DeepSeekService {
    
    private final DeepSeekConfig deepSeekConfig;
    private final ObjectMapper objectMapper;
    private final OkHttpClient client;

    public DeepSeekService(DeepSeekConfig deepSeekConfig) {
        this.deepSeekConfig = deepSeekConfig;
        this.objectMapper = new ObjectMapper();
        this.client = new OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .build();
    }

    public SseEmitter chatStream(String userMessage) {
        SseEmitter emitter = new SseEmitter(-1L);
        
        new Thread(() -> {
            try {
                String url = deepSeekConfig.getBaseUrl() + "/chat/completions";
                
                Map<String, Object> message1 = new HashMap<>();
                message1.put("role", "system");
                message1.put("content", "You are a helpful medical assistant.");

                Map<String, Object> message2 = new HashMap<>();
                message2.put("role", "user");
                message2.put("content", userMessage);

                Map<String, Object> requestBody = new HashMap<>();
                requestBody.put("model", "deepseek-chat");
                requestBody.put("messages", Arrays.asList(message1, message2));
                requestBody.put("stream", true);

                RequestBody body = RequestBody.create(
                    okhttp3.MediaType.parse("application/json"),
                    objectMapper.writeValueAsString(requestBody)
                );

                Request request = new Request.Builder()
                    .url(url)
                    .addHeader("Authorization", "Bearer " + deepSeekConfig.getApiKey())
                    .post(body)
                    .build();

                try (Response response = client.newCall(request).execute()) {
                    if (!response.isSuccessful()) throw new IOException("Unexpected response " + response);
                    
                    try (ResponseBody responseBody = response.body()) {
                        if (responseBody == null) throw new IOException("Empty response");
                        
                        BufferedSource source = responseBody.source();
                        while (!source.exhausted()) {
                            String line = source.readUtf8Line();
                            if (line == null || line.isEmpty()) continue;
                            if (line.startsWith("data: ")) {
                                String json = line.substring(6);
                                if ("[DONE]".equals(json)) {
                                    emitter.complete();
                                    break;
                                }
                                JsonNode node = objectMapper.readTree(json);
                                if (node.has("choices") && node.get("choices").size() > 0) {
                                    String content = node.get("choices").get(0)
                                        .get("delta").get("content").asText("");
                                    if (!content.isEmpty()) {
                                        emitter.send(content);
                                    }
                                }
                            }
                        }
                    }
                }
            } catch (Exception e) {
                emitter.completeWithError(e);
            }
        }).start();

        return emitter;
    }

    public SseEmitter chatReasonerStream(String userMessage) {
        SseEmitter emitter = new SseEmitter(-1L);
        
        new Thread(() -> {
            try {
                String url = deepSeekConfig.getBaseUrl() + "/chat/completions";
                
                Map<String, Object> message1 = new HashMap<>();
                message1.put("role", "system");
                message1.put("content", "You are a medical reasoning assistant that helps analyze medical cases and provide detailed reasoning.");

                Map<String, Object> message2 = new HashMap<>();
                message2.put("role", "user");
                message2.put("content", userMessage);

                Map<String, Object> requestBody = new HashMap<>();
                requestBody.put("model", "deepseek-reasoner");
                requestBody.put("messages", Arrays.asList(message1, message2));
                requestBody.put("stream", true);
                requestBody.put("temperature", 0.7);
                requestBody.put("max_tokens", 2000);
                requestBody.put("top_p", 0.95);

                RequestBody body = RequestBody.create(
                    okhttp3.MediaType.parse("application/json"),
                    objectMapper.writeValueAsString(requestBody)
                );

                Request request = new Request.Builder()
                    .url(url)
                    .addHeader("Authorization", "Bearer " + deepSeekConfig.getApiKey())
                    .post(body)
                    .build();

                try (Response response = client.newCall(request).execute()) {
                    if (!response.isSuccessful()) throw new IOException("Unexpected response " + response);
                    
                    try (ResponseBody responseBody = response.body()) {
                        if (responseBody == null) throw new IOException("Empty response");
                        
                        BufferedSource source = responseBody.source();
                        while (!source.exhausted()) {
                            String line = source.readUtf8Line();
                            if (line == null || line.isEmpty()) continue;
                            if (line.startsWith("data: ")) {
                                String json = line.substring(6);
                                if ("[DONE]".equals(json)) {
                                    emitter.complete();
                                    break;
                                }
                                JsonNode node = objectMapper.readTree(json);
                                if (node.has("choices") && node.get("choices").size() > 0) {
                                    String content = node.get("choices").get(0)
                                        .get("delta").get("content").asText("");
                                    if (!content.isEmpty()) {
                                        emitter.send(content);
                                    }
                                }
                            }
                        }
                    }
                }
            } catch (Exception e) {
                emitter.completeWithError(e);
            }
        }).start();

        return emitter;
    }

    public SseEmitter localChatStream(String userMessage) {
        SseEmitter emitter = new SseEmitter(-1L);
        
        new Thread(() -> {
            try {
                String url = "http://localhost:11434/api/generate";
                
                Map<String, Object> requestBody = new HashMap<>();
                requestBody.put("model", "deepseek-r1:8b");
                requestBody.put("prompt", userMessage);
                requestBody.put("stream", true);
                requestBody.put("system", "You are a helpful medical assistant.");
                requestBody.put("raw", false);
                requestBody.put("keep_alive", "5m");

                RequestBody body = RequestBody.create(
                    MediaType.parse("application/json"),
                    objectMapper.writeValueAsString(requestBody)
                );

                Request request = new Request.Builder()
                    .url(url)
                    .post(body)
                    .build();

                try (Response response = client.newCall(request).execute()) {
                    if (!response.isSuccessful()) {
                        String errorBody = response.body() != null ? response.body().string() : "Unknown error";
                        throw new IOException("Unexpected response " + response.code() + ": " + errorBody);
                    }
                    
                    try (ResponseBody responseBody = response.body()) {
                        if (responseBody == null) throw new IOException("Empty response");
                        
                        BufferedSource source = responseBody.source();
                        while (!source.exhausted()) {
                            String line = source.readUtf8Line();
                            if (line == null || line.isEmpty()) continue;
                            
                            JsonNode node = objectMapper.readTree(line);
                            
                            if (node.has("error")) {
                                String error = node.get("error").asText();
                                throw new IOException("API error: " + error);
                            }
                            
                            if (node.has("done") && node.get("done").asBoolean()) {
                                emitter.complete();
                                break;
                            }
                            
                            if (node.has("response")) {
                                String content = node.get("response").asText();
                                if (!content.isEmpty()) {
                                    emitter.send(content);
                                }
                            }
                        }
                    }
                }
            } catch (Exception e) {
                emitter.completeWithError(e);
            }
        }).start();

        return emitter;
    }

    public SseEmitter localChatReasonerStream(String userMessage) {
        SseEmitter emitter = new SseEmitter(-1L);
        
        new Thread(() -> {
            try {
                String url = "http://localhost:11434/api/generate";
                
                Map<String, Object> requestBody = new HashMap<>();
                requestBody.put("model", "deepseek-r1:8b");
                requestBody.put("prompt", userMessage);
                requestBody.put("stream", true);
                requestBody.put("system", "You are a medical reasoning assistant that helps analyze medical cases and provide detailed reasoning.");
                requestBody.put("temperature", 0.7);
                requestBody.put("top_p", 0.95);
                requestBody.put("raw", false);
                requestBody.put("keep_alive", "5m");

                Map<String, Object> options = new HashMap<>();
                options.put("temperature", 0.7);
                options.put("top_p", 0.95);
                requestBody.put("options", options);

                RequestBody body = RequestBody.create(
                    MediaType.parse("application/json"),
                    objectMapper.writeValueAsString(requestBody)
                );

                Request request = new Request.Builder()
                    .url(url)
                    .post(body)
                    .build();

                try (Response response = client.newCall(request).execute()) {
                    if (!response.isSuccessful()) {
                        String errorBody = response.body() != null ? response.body().string() : "Unknown error";
                        throw new IOException("Unexpected response " + response.code() + ": " + errorBody);
                    }
                    
                    try (ResponseBody responseBody = response.body()) {
                        if (responseBody == null) throw new IOException("Empty response");
                        
                        BufferedSource source = responseBody.source();
                        while (!source.exhausted()) {
                            String line = source.readUtf8Line();
                            if (line == null || line.isEmpty()) continue;
                            
                            JsonNode node = objectMapper.readTree(line);
                            
                            if (node.has("error")) {
                                String error = node.get("error").asText();
                                throw new IOException("API error: " + error);
                            }
                            
                            if (node.has("done") && node.get("done").asBoolean()) {
                                emitter.complete();
                                break;
                            }
                            
                            if (node.has("response")) {
                                String content = node.get("response").asText();
                                if (!content.isEmpty()) {
                                    emitter.send(content);
                                }
                            }
                        }
                    }
                }
            } catch (Exception e) {
                emitter.completeWithError(e);
            }
        }).start();

        return emitter;
    }
} 