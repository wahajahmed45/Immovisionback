package com.example.immovision.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PropertyByCityDTO extends Property{

    private double longitude;
    private double latitude;

}
