package com.example.immovision.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
public class PropertyDashboardDTO {

    private UUID id;
    private String title;
    private String description;
    private String location;
    private String city;
    private String state;
    private String country;
    private String zip;
    private String createdAt;
    private String approvalStatus;
    private String approvalComment;
    private String lastUpdatedAt;
    private String imageUrl;
    private double price;
    private String priceLabel;

}
