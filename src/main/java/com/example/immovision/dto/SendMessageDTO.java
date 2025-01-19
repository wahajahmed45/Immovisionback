package com.example.immovision.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class SendMessageDTO {
    private UUID propertyId;
    private String content;
    private String senderEmail;
    private String receiverEmail;
}
