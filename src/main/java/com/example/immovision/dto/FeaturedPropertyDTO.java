package com.example.immovision.dto;

import com.example.immovision.entities.images.PropertyImages;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class FeaturedPropertyDTO {
    private String id;
    private String type;
    private String location;
    private String country;
    private String city;
    private String zip;
    private double price;
    private int roomCount;
    private boolean isAvailable;
    private boolean isForRent;
    private String title;
    private double livingArea;
    private double LandArea;
    private String description;
    private String status;
    private String priceLabel;
    private int bathroom;

    private List<PropertyImages> images;
}