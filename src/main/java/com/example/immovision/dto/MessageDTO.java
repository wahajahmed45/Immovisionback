package com.example.immovision.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class MessageDTO {
    private UUID id;
    private String content;
    private String senderEmail;
    private String receiverEmail;
    private LocalDateTime sentAt;
    private boolean isRead;
    private PropertyInfoDTO property;

    @Data
    public static class PropertyInfoDTO {
        private UUID id;
        private String title;
    }
}
