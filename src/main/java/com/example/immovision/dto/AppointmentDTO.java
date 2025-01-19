package com.example.immovision.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentDTO {
    private UUID propertyId;
    private String agentEmail;
    private String clientEmail;
    private LocalDateTime dateTime;
    private String comment;
}
