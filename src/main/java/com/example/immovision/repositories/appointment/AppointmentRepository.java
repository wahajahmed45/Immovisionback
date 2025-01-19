package com.example.immovision.repositories.appointment;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.immovision.entities.appointment.Appointment;
import com.example.immovision.entities.user.User;

/**
 * Repository pour gérer les rendez-vous.
 * Gère les opérations CRUD pour l'entité Appointment.
 */
@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, UUID> {

    /**
     * Trouve tous les rendez-vous d'un client, triés par date croissante.
     *
     * @param clientEmail Email du client
     * @return Liste des rendez-vous du client
     */
    List<Appointment> findByClientEmailOrderByDateTimeAsc(String clientEmail);

    /**
     * Trouve tous les rendez-vous d'un agent, triés par date croissante.
     *
     * @param agentEmail Email de l'agent
     * @return Liste des rendez-vous de l'agent
     */
    List<Appointment> findByAgentEmailOrderByDateTimeAsc(String agentEmail);

    /**
     * Trouve tous les rendez-vous pour une propriété, triés par date croissante.
     *
     * @param propertyId ID de la propriété
     * @return Liste des rendez-vous pour la propriété
     */
    List<Appointment> findByPropertyIdOrderByDateTimeAsc(UUID propertyId);

    /**
     * Vérifie si un agent a déjà un rendez-vous sur une plage horaire.
     *
     * @param agent L'agent à vérifier
     * @param start Début de la plage horaire
     * @param end Fin de la plage horaire
     * @return true si l'agent a déjà un rendez-vous, false sinon
     */
    boolean existsByAgentAndDateTimeBetween(
        User agent,
        LocalDateTime start, 
        LocalDateTime end
    );

    void deleteAllByProperty_Id(UUID propertyId);

}
