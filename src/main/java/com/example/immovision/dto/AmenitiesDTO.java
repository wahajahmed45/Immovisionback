package com.example.immovision.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class AmenitiesDTO {
    private List<String> interior = new ArrayList<>();
    private List<String> exterior = new ArrayList<>();
    private List<String> other = new ArrayList<>();
}