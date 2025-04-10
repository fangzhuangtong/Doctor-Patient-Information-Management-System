package com.example.hello.controller;

import com.example.hello.service.DeepSeekService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/deepseek")
public class DeepSeekController {

    private final DeepSeekService deepSeekService;

    public DeepSeekController(DeepSeekService deepSeekService) {
        this.deepSeekService = deepSeekService;
    }

    @PostMapping("/chat/stream")
    public SseEmitter chatStream(@RequestBody String message) {
        return deepSeekService.chatStream(message);
    }

    @PostMapping("/chat-reasoner/stream")
    public SseEmitter chatReasonerStream(@RequestBody String message) {
        return deepSeekService.chatReasonerStream(message);
    }

    @PostMapping("/local/chat/stream")
    public SseEmitter localChatStream(@RequestBody String message) {
        return deepSeekService.localChatStream(message);
    }

    @PostMapping("/local/chat-reasoner/stream")
    public SseEmitter localChatReasonerStream(@RequestBody String message) {
        return deepSeekService.localChatReasonerStream(message);
    }
} 