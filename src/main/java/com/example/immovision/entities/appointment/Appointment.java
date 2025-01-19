package com.example.immovision.entities.appointment;

import com.example.immovision.entities.property.Property;
import com.example.immovision.entities.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;

import java.sql.Types;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "appointments")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Appointment {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private LocalDateTime dateTime;

    @ManyToOne
    @JoinColumn(name = "property_id", nullable = false)
    private Property property;

    @ManyToOne
    @JoinColumn(name = "agent_id", nullable = false)
    private User agent;

    @Column(name = "client_email", nullable = false)
    private String clientEmail;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AppointmentStatus status;

    @Column(name = "comment")
    private String comment;  // Pour les raisons de refus ou d'annulation ou autres

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public enum AppointmentStatus {
        PENDING,    // En attente de confirmation
        APPROVED,   // Rendez-vous confirmé
        REJECTED,   // Rendez-vous refusé
        CANCELLED   // Rendez-vous annulé
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

}



