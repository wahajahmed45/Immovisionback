package com.example.immovision.entities.property;

import com.example.immovision.entities.amenity.Amenity;
import com.example.immovision.entities.appointment.Appointment;
import com.example.immovision.entities.images.PropertyImages;
import com.example.immovision.entities.user.User;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;

import javax.annotation.Nullable;
import java.sql.Types;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@Entity
@Table(name = "properties")
public class Property {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @JdbcTypeCode(Types.VARCHAR)
    @Column(name = "id_property", unique = true)
    private UUID id;

    @Column(nullable = true)
    private String title;

    @Column(length = 1000, nullable = true)
    private String description;

    @Column(nullable = true)
    private String type;

    @Column(nullable = true)
    private String status;

    @Column(nullable = true)
    private String category;

    @Column(nullable = true)
    private Double price;  // Changed to Double from double to allow null

    @Column(nullable = true)
    private String priceLabel;

    @Column(nullable = true)
    private String pricePrefix;

    @Column(nullable = true)
    private String yearlyTaxRate;

    @Column(nullable = true)
    private String hoaFee;

    @Column(nullable = true)
    private Integer bathroom;  // Changed to Integer from int to allow null

    @Column(nullable = true)
    private Double livingArea;

    @Column(nullable = true)
    private Double landArea;

    @Column(nullable = true)
    private Integer garage;

    @Column(nullable = true)
    private String garageSize;

    @Column(nullable = true)
    private Integer bedroom;

    @Column(nullable = true)
    private Integer roomCounts;

    @Column(nullable = true)
    private Integer toilet;

    @Column(nullable = true)
    private Integer floorNumber;

    @Column(nullable = true)
    private String buildingCondition;

    @Column(nullable = true)
    private String typeOfEnvironnement;

    @Column(nullable = true)
    private Boolean attic;

    @Column(nullable = true)
    private Boolean garden;

    @Column(nullable = true)
    private Double gardenArea;

    @Column(nullable = true)
    private Boolean Parking;

    @Column(nullable = true)
    private String yearBuilt;

    @Column(nullable = true)
    private LocalDate availableFrom;

    @Column(nullable = true)
    private Boolean basement;

    @Column(nullable = true)
    private String extraDetails;

    @Column(nullable = true)
    private String location;  // Changed from nullable = false

    @Column(nullable = true)
    private String country;

    @Column(nullable = true)
    private String state;


    private Boolean isApproved;

    @Column(nullable = true)
    private String city;

    @Column(nullable = true)
    private String zip;

    @Column(nullable = true)
    private String virtualTour;

    @Column(nullable = true)
    private String propertyVideo;

    @Column(nullable = true)
    private String floorPlan;

    @Column(nullable = true)
    private Boolean isAvailable;

    @Column(nullable = true)
    private Boolean isForRent;

    @Column(nullable = true)
    private Boolean isFeatured;

    @Column(name = "created_at", nullable = true)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = true)
    private LocalDateTime updatedAt;
    @Column(name = "longitude", nullable = true)
    private double longitude;
    @Column(name = "latitude", nullable = true)
    private double latitude;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ApprovalStatus approvalStatus = ApprovalStatus.PENDING;

    @Column(columnDefinition = "TEXT")
    private String approvationComment;

    @ManyToOne
    @JoinColumn(name = "owner_id")
    @JsonManagedReference
    private User owner;

    @ManyToOne
    @JoinColumn(name = "agent_id")
    @JsonManagedReference
    private User agent;
    @OneToMany(mappedBy = "property", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Appointment> appointments;

    @OneToMany(mappedBy = "property", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PropertyImages> images;

    @OneToOne(mappedBy = "property", cascade = CascadeType.ALL, orphanRemoval = true)
    private Amenity amenities;
    @Nullable
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public enum ApprovalStatus {
        PENDING,
        APPROVED,
        REJECTED
    }

}