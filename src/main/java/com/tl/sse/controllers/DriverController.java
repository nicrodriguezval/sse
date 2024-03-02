package com.tl.sse.controllers;

import com.tl.sse.dtos.DriverPositionDTO;
import com.tl.sse.services.SSEService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@AllArgsConstructor
@RequestMapping("/drivers")
@Controller
public class DriverController {
    private final SSEService sseService;

    @PatchMapping("/{id}/update-position")
    public ResponseEntity<Void> updateDriverPosition(
            @PathVariable String id,
            @RequestBody DriverPositionDTO body
    ) {
        log.info("Received position update for driver {} with position {}", id, body.getPosition());
        // ... here we would update the driver's position in the database
        // ... and then send the updated position to all clients
        sseService.sendDriverPositionUpdate(id, body);
        return ResponseEntity.ok().build();
    }
}
