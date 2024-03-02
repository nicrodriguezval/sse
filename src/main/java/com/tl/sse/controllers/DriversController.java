package com.tl.sse.controllers;

import com.tl.sse.dtos.DriverPositionDTO;
import com.tl.sse.services.DriversService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Slf4j
@AllArgsConstructor
@RequestMapping("/drivers")
@Controller
public class DriversController {
    private final DriversService driversService;

    @GetMapping(
            path = "/{id}/position/stream",
            produces = MediaType.TEXT_EVENT_STREAM_VALUE
    )
    public SseEmitter streamDriverPosition(@PathVariable String id) {
        log.info("Received request to stream driver position for driver {}", id);
        return driversService.createDriverPositionEmitter(id);
    }

    @PatchMapping("/{id}/position")
    public ResponseEntity<Void> updateDriverPosition(
            @PathVariable String id,
            @RequestBody DriverPositionDTO position
    ) {
        log.info("Received position update for driver {} with position {}", id, position);
        // ... here we would update the driver's position in the database,
        // and then, send the updated position to all clients
        driversService.sendDriverPositionUpdate(id, position);
        return ResponseEntity.ok().build();
    }
}
