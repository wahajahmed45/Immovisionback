package com.example.immovision.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SlotDTO {
    private UUID slotId;
    private UUID appointmentId;
    private String time;
}
