package com.example.immovision.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReviewDTO {
    private UUID id;
    private UUID propertyId;
    private String agentEmail;
    private String userEmail;
    private String userName;
    private Integer propertyRating;
    private Integer agentRating;
    private LocalDateTime reviewDate;
    private String comment;
}
