package com.example.immovision.controllers.appointment;

import com.example.immovision.dto.AppointmentDTO;
import com.example.immovision.dto.AppointmentResponseDTO;
import com.example.immovision.entities.appointment.Appointment;
import com.example.immovision.services.AppointmentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/appointments")
@Slf4j
public class AppointmentController {
    @Autowired
    private AppointmentService appointmentService;

    // Créer un RDV
    @PostMapping
    public ResponseEntity<?> createAppointment(@RequestBody AppointmentDTO appointmentDTO) {
        try {
            appointmentService.createAppointment(appointmentDTO);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (RuntimeException e) {
            if (e.getMessage().equals("Agent is not available at this time")) {
                return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body("Agent is not available at this time");
            }
            log.error("Error creating appointment", e);
            return ResponseEntity.badRequest().build();
        }
    }

    // Mettre à jour le statut d'un RDV
    @PutMapping("/{id}/status")
    public ResponseEntity<AppointmentResponseDTO> updateAppointmentStatus(
            @PathVariable UUID id,
            @RequestParam Appointment.AppointmentStatus status,
            @RequestParam(required = false) String comment) {
        try {
            AppointmentResponseDTO dto = appointmentService.updateAppointmentStatus(id, status, comment);
            return ResponseEntity.ok(dto);
        } catch (Exception e) {
            log.error("Error updating appointment status", e);
            return ResponseEntity.badRequest().build();
        }
    }

    // Obtenir les RDV d'un client
    @GetMapping("/client/{email}")
    public ResponseEntity<List<AppointmentResponseDTO>> getClientAppointments(@PathVariable String email) {
        try {
            List<AppointmentResponseDTO> appointments = appointmentService.getClientAppointments(email);
            return ResponseEntity.ok(appointments);
        } catch (Exception e) {
            log.error("Error getting client appointments", e);
            return ResponseEntity.badRequest().build();
        }
    }

    // Obtenir les RDV d'un agent
    @GetMapping("/agent/{email}")
    public ResponseEntity<List<AppointmentResponseDTO>> getAgentAppointments(@PathVariable String email) {
        try {
            List<AppointmentResponseDTO> appointments = appointmentService.getAgentAppointments(email);
            return ResponseEntity.ok(appointments);
        } catch (Exception e) {
            log.error("Error getting agent appointments", e);
            return ResponseEntity.badRequest().build();
        }
    }

    // Obtenir les RDV d'une propriété
    @GetMapping("/property/{propertyId}")
    public ResponseEntity<List<AppointmentResponseDTO>> getPropertyAppointments(@PathVariable UUID propertyId) {
        try {
            List<AppointmentResponseDTO> appointments = appointmentService.getPropertyAppointmentsDTO(propertyId);
            return ResponseEntity.ok(appointments);
        } catch (Exception e) {
            log.error("Error getting property appointments", e);
            return ResponseEntity.badRequest().build();
        }
    }

    // Obtenir un RDV spécifique
    @GetMapping("/{id}")
    public ResponseEntity<Appointment> getAppointment(@PathVariable UUID id) {
        try {
            return appointmentService.getAppointment(id)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            log.error("Error getting appointment", e);
            return ResponseEntity.badRequest().build();
        }
    }
}