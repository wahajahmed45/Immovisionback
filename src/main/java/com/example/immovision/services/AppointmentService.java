package com.example.immovision.services;

import com.example.immovision.dto.AppointmentDTO;
import com.example.immovision.dto.AppointmentResponseDTO;
import com.example.immovision.entities.appointment.Appointment;
import com.example.immovision.entities.property.Property;
import com.example.immovision.entities.user.User;
import com.example.immovision.repositories.appointment.AppointmentRepository;
import com.example.immovision.repositories.property.PropertyRepository;
import com.example.immovision.repositories.user.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
public class AppointmentService {
    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private UserService userService;
    @Autowired
    private PropertyRepository propertyRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private MessageService messageService;

    /**
     * Crée un nouveau rendez-vous.
     * Vérifie la disponibilité de l'agent et envoie une notification.
     *
     * @param appointmentDTO Les données du rendez-vous à créer
     * @return Le rendez-vous créé
     * @throws RuntimeException si l'agent ou la propriété n'existe pas, ou si l'agent n'est pas disponible
     */
    @Transactional
    public Appointment createAppointment(AppointmentDTO appointmentDTO) {
        // Vérifier que l'agent existe
        User agent = userService.findByEmail(appointmentDTO.getAgentEmail())
                .orElseThrow(() -> new RuntimeException("Agent not found"));

        // Vérifier que la propriété existe
        Property property = propertyRepository.findById(appointmentDTO.getPropertyId())
                .orElseThrow(() -> new RuntimeException("Property not found"));

        // Vérifier si l'agent est disponible
        if (isAgentBusy(agent, appointmentDTO.getDateTime())) {
            throw new RuntimeException("Agent is not available at this time");
        }

        Appointment appointment = new Appointment();
        appointment.setDateTime(appointmentDTO.getDateTime());
        appointment.setProperty(property);
        appointment.setAgent(agent);
        appointment.setClientEmail(appointmentDTO.getClientEmail());
        appointment.setStatus(Appointment.AppointmentStatus.PENDING);
        appointment.setComment(appointmentDTO.getComment());

        Appointment savedAppointment = appointmentRepository.save(appointment);
        sendAppointmentNotification(savedAppointment, Appointment.AppointmentStatus.PENDING.toString());
        return savedAppointment;
    }

    /**
     * Met à jour le statut d'un rendez-vous et envoie une notification appropriée.
     *
     * @param appointmentId ID du rendez-vous
     * @param status        Nouveau statut (APPROVED, REJECTED, CANCELLED)
     * @param comment       Commentaire optionnel (raison du rejet/annulation)
     * @return DTO avec les informations mises à jour
     * @throws RuntimeException si le rendez-vous n'existe pas
     */
    @Transactional
    public AppointmentResponseDTO updateAppointmentStatus(UUID appointmentId, Appointment.AppointmentStatus status, String comment) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));

        appointment.setStatus(status);
        if (comment != null && !comment.isEmpty()) {
            appointment.setComment(comment);
        }

        Appointment updatedAppointment = appointmentRepository.save(appointment);
        sendAppointmentNotification(updatedAppointment, status.toString());
        return convertToDTO(updatedAppointment);
    }

    /**
     * Vérifie si un agent est disponible à une date/heure donnée.
     * Vérifie 30 minutes avant et après le créneau demandé.
     *
     * @param agent    L'agent à vérifier
     * @param dateTime La date et l'heure du rendez-vous
     * @return true si l'agent est occupé, false sinon
     */
    private boolean isAgentBusy(User agent, LocalDateTime dateTime) {
        // On vérifie 30 minutes avant et après
        LocalDateTime start = dateTime.minusMinutes(30);
        LocalDateTime end = dateTime.plusMinutes(30);
        return appointmentRepository.existsByAgentAndDateTimeBetween(agent, start, end);
    }

    /**
     * Récupère tous les rendez-vous d'un client.
     *
     * @param clientEmail Email du client
     * @return Liste des rendez-vous du client
     */
    public List<AppointmentResponseDTO> getClientAppointments(String clientEmail) {
        return appointmentRepository.findByClientEmailOrderByDateTimeAsc(clientEmail)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Récupère tous les rendez-vous d'un agent.
     *
     * @param agentEmail Email de l'agent
     * @return Liste des rendez-vous de l'agent
     */
    public List<AppointmentResponseDTO> getAgentAppointments(String agentEmail) {
        return appointmentRepository.findByAgentEmailOrderByDateTimeAsc(agentEmail)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Convertit un rendez-vous en DTO avec les informations détaillées.
     *
     * @param appointment Le rendez-vous à convertir
     * @return DTO contenant les informations du rendez-vous
     */
    private AppointmentResponseDTO convertToDTO(Appointment appointment) {
        AppointmentResponseDTO dto = new AppointmentResponseDTO();
        String userName = userRepository.findByEmail(appointment.getClientEmail())
                .map(User::getName)
                .orElse("Anonym");

        dto.setId(appointment.getId());
        dto.setDateTime(appointment.getDateTime());
        dto.setStatus(appointment.getStatus().toString());
        dto.setComment(appointment.getComment());
        dto.setClientEmail(appointment.getClientEmail());
        dto.setClientName(userName);


        AppointmentResponseDTO.PropertySimpleDTO propertyDTO = new AppointmentResponseDTO.PropertySimpleDTO();
        propertyDTO.setId(appointment.getProperty().getId());
        propertyDTO.setTitle(appointment.getProperty().getTitle());
        propertyDTO.setLocation(appointment.getProperty().getLocation());

        // Récupérer juste la première image
        propertyDTO.setImage(appointment.getProperty().getImages().isEmpty()
                ? null
                : appointment.getProperty().getImages().get(0).getImage_url());

        dto.setProperty(propertyDTO);

        AppointmentResponseDTO.AgentSimpleDTO agentDTO = new AppointmentResponseDTO.AgentSimpleDTO();
        agentDTO.setEmail(appointment.getAgent().getEmail());
        agentDTO.setName(appointment.getAgent().getName());
        dto.setAgent(agentDTO);

        return dto;
    }

    /**
     * Récupère tous les rendez-vous d'une propriété.
     *
     * @param propertyId ID de la propriété
     * @return Liste des rendez-vous de la propriété
     */
    public List<AppointmentResponseDTO> getPropertyAppointmentsDTO(UUID propertyId) {
        List<Appointment> appointments = appointmentRepository.findByPropertyIdOrderByDateTimeAsc(propertyId);
        return appointments.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Récupère un rendez-vous par son ID.
     *
     * @param id ID du rendez-vous
     * @return Le rendez-vous s'il existe
     */
    public Optional<Appointment> getAppointment(UUID id) {
        return appointmentRepository.findById(id);
    }

    /**
     * Envoie une notification pour un changement de statut de rendez-vous.
     *
     * @param appointment      Le rendez-vous concerné
     * @param notificationType Le type de notification (PENDING, APPROVED, REJECTED, CANCELLED)
     */
    private void sendAppointmentNotification(Appointment appointment, String notificationType) {
        String userName = userRepository.findByEmail(appointment.getClientEmail())
                .map(User::getName)
                .orElse("Anonym");
        String message = switch (notificationType) {
            case "PENDING" -> String.format(
                    "Agent %s suggests an appointment on %s",
                    appointment.getAgent().getName(),
                    formatDateTime(appointment.getDateTime())
            );
            case "APPROVED" -> String.format(
                    "Appointment on %s has been accepted by %s",
                    formatDateTime(appointment.getDateTime()),
                    userName
            );
            case "REJECTED" -> String.format(
                    "Appointment on %s has been declined by %s. Reason: %s",
                    formatDateTime(appointment.getDateTime()),
                    userName,
                    appointment.getComment()
            );
            case "CANCELLED" -> String.format(
                    "Appointment on %s has been cancelled by %s. Reason: %s",
                    formatDateTime(appointment.getDateTime()),
                    userName,
                    appointment.getComment()
            );
            default -> throw new IllegalArgumentException("Invalid notification type");
        };

        // Envoyer le message système
        messageService.sendSystemMessage(
                message,
                appointment.getAgent().getEmail(),
                appointment.getClientEmail(),
                appointment.getProperty().getId().toString(),
                notificationType
        );
    }

    /**
     * Formate une date et heure en chaîne lisible.
     *
     * @param dateTime La date et l'heure à formater
     * @return Chaîne formatée (ex: "25/12/2024 à 14:30")
     */
    private String formatDateTime(LocalDateTime dateTime) {
        return String.format("%02d/%02d/%d à %02d:%02d",
                dateTime.getDayOfMonth(),
                dateTime.getMonthValue(),
                dateTime.getYear(),
                dateTime.getHour(),
                dateTime.getMinute());
    }
}