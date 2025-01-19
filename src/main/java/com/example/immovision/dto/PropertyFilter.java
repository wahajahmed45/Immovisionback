package com.example.immovision.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PropertyFilter {
    private Integer priceMin;
    private Integer priceMax;
    private Integer rooms;
    private Integer bathrooms;
    private String city;

}
