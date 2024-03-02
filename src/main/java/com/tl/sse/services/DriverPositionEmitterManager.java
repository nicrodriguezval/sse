package com.tl.sse.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;

@Slf4j
public class DriverPositionEmitterManager {
    private static final HashMap<String, List<SseEmitter>> driverPositionEmitters = new HashMap<>();

    public static Optional<List<SseEmitter>> getEmittersByDriverId(String driverId) {
        return Optional.ofNullable(driverPositionEmitters.get(driverId));
    }

    public static void addEmitter(String driverId, SseEmitter emitter) {
        var emitters = driverPositionEmitters.computeIfAbsent(driverId, k -> new CopyOnWriteArrayList<>());
        emitters.add(emitter);
    }

    public static void removeEmitter(String driverId, SseEmitter emitter) {
        var optEmitters = getEmittersByDriverId(driverId);

        if (optEmitters.isEmpty()) {
            log.warn("No emitters found for driver {}", driverId);
            return;
        }

        var emitters = optEmitters.get();

        emitters.remove(emitter);

        if (emitters.isEmpty()) {
            driverPositionEmitters.remove(driverId);
        }
    }
}
