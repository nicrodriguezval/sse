package com.tl.sse.dtos;

import lombok.Data;

@Data
public class DriverPositionDTO {
    private Position position;

    @Data
    private static class Position {
        private double latitude;
        private double longitude;
    }
}
