package com.example.hmsbe.service;

import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.*;

@Service
public class NotificationService {
    // Map studentId -> list of emitters for that student
    private final Map<Long, List<SseEmitter>> emitters = Collections.synchronizedMap(new HashMap<>());

    public SseEmitter subscribeForUser(Long studentId) {
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
        emitters.computeIfAbsent(studentId, k -> Collections.synchronizedList(new ArrayList<>())).add(emitter);
        emitter.onCompletion(() -> removeEmitter(studentId, emitter));
        emitter.onTimeout(() -> removeEmitter(studentId, emitter));
        return emitter;
    }

    private void removeEmitter(Long studentId, SseEmitter emitter) {
        List<SseEmitter> list = emitters.get(studentId);
        if (list != null) {
            list.remove(emitter);
            if (list.isEmpty()) emitters.remove(studentId);
        }
    }

    public void sendRoomAssignment(Long studentId, String roomNo) {
        List<SseEmitter> list = emitters.get(studentId);
        if (list == null || list.isEmpty()) return;
        List<SseEmitter> dead = new ArrayList<>();
        synchronized (list) {
            for (SseEmitter emitter : list) {
                try {
                    Map<String, Object> payload = new HashMap<>();
                    payload.put("studentId", studentId);
                    payload.put("roomNo", roomNo);
                    emitter.send(SseEmitter.event().name("room-assigned").data(payload));
                } catch (Exception ex) {
                    dead.add(emitter);
                }
            }
            list.removeAll(dead);
        }
    }
}
