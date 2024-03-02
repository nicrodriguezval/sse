package com.tl.sse.services;

import com.tl.sse.dtos.DriverPositionDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Slf4j
@Service
public class SSEService {
    private final HashMap<String, List<SseEmitter>> driverPositionEmitters = new HashMap<>();

    public SseEmitter createDriverPositionEmitter(String driverId) {
        var emitter = new SseEmitter(Long.MAX_VALUE);

        var emitters = driverPositionEmitters.computeIfAbsent(driverId, k -> new CopyOnWriteArrayList<>());

        emitters.add(emitter);
        emitter.onCompletion(() -> emitters.remove(emitter));
        emitter.onTimeout(emitter::complete);

        return emitter;
    }

    @Async
    public void sendDriverPositionUpdate(String driverId, DriverPositionDTO positionDto) {
        var emitters = driverPositionEmitters.get(driverId);

        if (emitters == null) {
            log.warn("No emitters found for driver {}", driverId);
            return;
        }

        emitters.forEach(emitter -> {
            try {
                var event = SseEmitter.event()
                        .name("position")
                        .data(positionDto, MediaType.APPLICATION_JSON);

                emitter.send(event);
            } catch (IOException e) {
                try {
                    emitter.completeWithError(e);
                    log.info("Marked SseEmitter as complete with an error");
                } catch (Exception completionException) {
                    log.error("Failed to mark SseEmitter as complete on error");
                }
            } catch (Exception e) {
                log.error("Error sending driver position update: {}", e.getMessage());
            }
        });
    }
}
