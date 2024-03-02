package com.tl.sse.controllers;

import com.tl.sse.services.SSEService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Slf4j
@AllArgsConstructor
@RequestMapping("/stream")
@RestController
public class SSEController {
    private final SSEService sseService;

    @GetMapping(
            path = "/driver/{id}/position",
            produces = MediaType.TEXT_EVENT_STREAM_VALUE
    )
    public SseEmitter streamDriverPosition(@PathVariable String id) {
        log.info("Received request to stream driver position for driver {}", id);
        return sseService.createDriverPositionEmitter(id);
    }
}
