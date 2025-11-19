package com.example.hmsbe.controller;

import com.example.hmsbe.service.NotificationService;
import com.example.hmsbe.repo.StudentRepository;
import com.example.hmsbe.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private static final Logger log = LoggerFactory.getLogger(NotificationController.class);

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private StudentRepository studentRepository;

    @GetMapping("/subscribe")
    public SseEmitter subscribe(@RequestParam(value = "token", required = false) String token) {
        // EventSource cannot set custom headers in browsers, so we accept JWT as query param.
        if (token == null || token.isBlank()) {
            log.warn("Subscribe called without token");
            return new SseEmitter(0L); // immediate timeout empty emitter
        }
        try {
            String username = jwtUtil.extractUsername(token);
            if (username == null) {
                log.warn("Token did not contain username");
                return new SseEmitter(0L);
            }
            return studentRepository.findByUsername(username)
                    .map(s -> notificationService.subscribeForUser(s.getId()))
                    .orElseGet(() -> {
                        log.warn("No student found for username={}", username);
                        return new SseEmitter(0L);
                    });
        } catch (Exception ex) {
            log.error("Failed to subscribe SSE for token", ex);
            return new SseEmitter(0L);
        }
    }
}
