package com.example.immovision.repositories.amenity;

import com.example.immovision.entities.amenity.Amenity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AmenityRepository extends JpaRepository<Amenity, UUID> {
}
