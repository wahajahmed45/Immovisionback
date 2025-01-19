package com.example.immovision.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PropertyDTO {
    private UUID propertyId;
    private String title;
    private String description;
    private String type;
    private String status;
    private String category;
    private Double price;
    private String priceLabel;
    private String pricePrefix;
    private String yearlyTaxRate;
    private String hoaFee;
    private Integer roomCounts;
    private Integer bathroom;
    private String yearBuilt;
    private LocalDate availableFrom;
    private Boolean basement;
    private String extraDetails;
    private String location;
    private String country;
    private String state;
    private String city;
    private String zip;
    private String virtualTour;
    private String propertyVideo;
    private String floorPlan;
    private Boolean available;
    private Boolean forRent;
    private String[] imageUrl;  // URL returned from Cloudinary

    private Double livingArea;
    private Double LandArea;
    private Integer garage; //
    private String garageSize; //
    private Integer bedroom;
    private Integer toilet;
    private Integer floorNumber;
    private String buildingCondition;
    private String typeOfEnvironnement;
    private Boolean attic;
    private Boolean garden;
    private Double gardenArea;
    private Boolean Parking;
    private LocalDateTime updatedAt;
    private String agentEmail;
    private String agentName;
    private Boolean isApproved;
    private String ownerEmail;
    private String approvationStatus;
    private String approvationComment;

    @JsonProperty("amenities")
    private AmenitiesDTO amenities;

    private MultipartFile[] images;

}
