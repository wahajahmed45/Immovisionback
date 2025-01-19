package com.example.immovision.dto;

import jakarta.persistence.Transient;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = true)
public class UpdatePropertyDTO extends CreatePropertyDTO {
    private UUID propertyId;  // Nécessaire pour identifier la propriété à mettre à jour
    
    @Transient
    private List<String> imagesToDelete;
} 