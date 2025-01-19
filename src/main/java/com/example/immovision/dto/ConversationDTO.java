package com.example.immovision.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class ConversationDTO {
    private UUID id;
    private LastMessageDTO lastMessage;
    private PropertyInfoDTO property;
    private ParticipantDTO participant;

    @Data
    public static class LastMessageDTO {
        private String senderEmail;
        private String content;
        private LocalDateTime sentAt;
        private boolean isRead;
    }

    @Data
    public static class PropertyInfoDTO {
        private UUID id;
        private String title;
        private String imageUrl;
        private String ownerEmail;
        private String agentEmail;
    }

    @Data
    public static class ParticipantDTO {
        private String email;
        private String name;
        private String role;
    }
}
