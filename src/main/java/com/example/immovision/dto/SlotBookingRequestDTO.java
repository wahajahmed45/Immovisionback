package com.example.immovision.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SlotBookingRequestDTO {

    private UUID slotId;

    private String name;

    private String phone;

    private String email;


}