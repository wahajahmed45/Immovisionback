package com.example.immovision.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
public class AppointmentResponseDTO {
    private UUID id;
    private LocalDateTime dateTime;
    private String status;
    private String comment;
    private String clientEmail;
    private String clientName;
    private PropertySimpleDTO property;
    private AgentSimpleDTO agent;
    
    @Data
    public static class PropertySimpleDTO {
        private UUID id;
        private String title;
        private String image;
        private String location;
    }
    
    @Data
    public static class PropertyImageDTO {
        private String url;
    }
    
    @Data
    public static class AgentSimpleDTO {
        private String email;
        private String name;
    }
}
