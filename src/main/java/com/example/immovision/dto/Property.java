package com.example.immovision.dto;

import com.example.immovision.entities.images.PropertyImages;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
public abstract class Property {
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
    private int bathroom;

    private List<PropertyImages> images;
}
