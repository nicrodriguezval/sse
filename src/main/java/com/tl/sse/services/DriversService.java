package com.tl.sse.services;

import com.tl.sse.dtos.DriverPositionDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;

@Slf4j
@Service
public class DriversService {

    public SseEmitter createDriverPositionEmitter(String driverId) {
        var emitter = new SseEmitter(Long.MAX_VALUE);

        DriverPositionEmitterManager.addEmitter(driverId, emitter);
        emitter.onCompletion(() -> DriverPositionEmitterManager.removeEmitter(driverId, emitter));
        emitter.onTimeout(() -> DriverPositionEmitterManager.removeEmitter(driverId, emitter));

        return emitter;
    }

    @Async
    public void sendDriverPositionUpdate(String driverId, DriverPositionDTO position) {
        var optEmitters = DriverPositionEmitterManager.getEmittersByDriverId(driverId);

        if (optEmitters.isEmpty()) {
            log.warn("No emitters found for driver {}", driverId);
            return;
        }

        optEmitters.get().forEach(emitter -> {
            try {
                var event = SseEmitter.event()
                        .name("position")
                        .data(position, MediaType.APPLICATION_JSON);

                emitter.send(event);
            } catch (IOException e) {
                // When this happens, the connection is PROBABLY closed
                try {
                    emitter.completeWithError(e);
                    log.info("Marked SseEmitter as completed");
                } catch (Exception completionException) {
                    log.error("Failed to mark SseEmitter as completed");
                }
            } catch (Exception e) {
                log.error("Error sending driver position update: {}", e.getMessage());
            }
        });
    }
}
