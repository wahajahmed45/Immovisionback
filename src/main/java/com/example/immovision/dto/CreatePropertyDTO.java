package com.example.immovision.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class CreatePropertyDTO {
    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Description is required")
    private String description;

    @NotBlank(message = "Property type is required")
    private String type;

    @NotBlank(message = "Status is required")
    private String status;

    private String category;

    @Positive(message = "Price must be positive")
    private Double price;

    private String priceLabel;
    private String pricePrefix;
    private String yearlyTaxRate;
    private String hoaFee;

    @Min(value = 0, message = "Room count must be positive")
    private Integer roomCount;

    @Min(value = 0, message = "Bathroom count must be positive")
    private Integer bathroom;

    @Positive(message = "Area must be positive")
    private Double livingArea;

    @Positive(message = "Area must be positive")
    private Double LandArea;

    @Min(value = 0, message = "garage count must be positive")
    private Integer garage;

    private String garageSize;

    @Min(value = 0, message = "bedroom count must be positive")
    private Integer bedroom;

    @Min(value = 0, message = "toilet count must be positive")
    private Integer toilet;

    @Min(value = 0, message = "floorNumber count must be positive")
    private Integer floorNumber;

    @NotBlank(message = "building Condition is required")
    private String buildingCondition;

    @NotBlank(message = "Type Of Environnement Condition is required")
    private String TypeOfEnvironnement;

    private Boolean attic;

    private Boolean garden;

    @Positive(message = "Area must be positive")
    private Double gardenArea;

    private Boolean Parking;

    private String yearBuilt;

    private LocalDate availableFrom;

    private Boolean basement;
    private String extraDetails;

    @NotBlank(message = "Location is required")
    private String location;

    @NotBlank(message = "Country is required")
    private String country;

    @NotBlank(message = "State is required")
    private String state;

    @NotBlank(message = "City is required")
    private String city;

    @NotBlank(message = "ZIP code is required")
    private String zip;

    private String ownerEmail;

    private String virtualTour;
    private String propertyVideo;
    private String floorPlan;

    private Boolean available = true;
    private Boolean forRent = false;

    @JsonProperty("amenities")
    private AmenitiesDTO amenities;

    private MultipartFile[] images;

}
