package com.example.immovision;

import com.example.immovision.dto.PropertyDTO;
import com.example.immovision.entities.property.Property;
import com.example.immovision.repositories.property.PropertyRepository;
import com.example.immovision.services.PropertyService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PropertyTests {

    @Mock
    private PropertyRepository propertyRepository;

    @InjectMocks
    private PropertyService propertyService;

//    @Test
//    void updateProperty_ShouldUpdateFields() {
//        // Arrange
//        UUID propertyId = UUID.randomUUID();
//
//        // Créer une propriété existante
//        Property existingProperty = new Property();
//        existingProperty.setId(propertyId);
//        existingProperty.setTitle("Old Title");
//        existingProperty.setPrice(100000.0);
//        existingProperty.setBathroom(2);
//        existingProperty.setApprovalStatus(Property.ApprovalStatus.APPROVED);
//
//        // Créer le DTO avec les nouvelles valeurs
//        PropertyDTO updateDTO = new PropertyDTO();
//        updateDTO.setPropertyId(propertyId);
//        updateDTO.setTitle("New Title");
//        updateDTO.setPrice(150000.0);
//        updateDTO.setBathroom(3);
//
//        // Mock repository responses
//        when(propertyRepository.findById(propertyId)).thenReturn(Optional.of(existingProperty));
//        when(propertyRepository.save(any(Property.class))).thenAnswer(i -> i.getArguments()[0]);
//
//        // Act
//        Property updatedProperty = propertyService.updateProperty(updateDTO);
//
//        // Assert
//        assertNotNull(updatedProperty);
//        assertEquals(existingProperty.getId(), updatedProperty.getId());
//        assertEquals(existingProperty.getApprovalStatus(), updatedProperty.getApprovalStatus());
//        assertEquals("New Title", updatedProperty.getTitle());
//        assertEquals(150000.0, updatedProperty.getPrice());
//        assertEquals(3, updatedProperty.getBathroom());
//    }
//
//    @Test
//    void updateProperty_WithNonExistentId_ShouldThrowException() {
//        // Arrange
//        UUID nonExistentId = UUID.randomUUID();
//        PropertyDTO updateDTO = new PropertyDTO();
//        updateDTO.setPropertyId(nonExistentId);
//
//        when(propertyRepository.findById(nonExistentId)).thenReturn(Optional.empty());
//
//        // Act & Assert
//        assertThrows(EntityNotFoundException.class, () -> {
//            propertyService.updateProperty(updateDTO);
//        });
//    }
}
