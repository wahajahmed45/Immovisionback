package com.example.immovision.repositories.propertyImage;

import com.example.immovision.entities.images.PropertyImages;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PropertyImageRepository extends JpaRepository<PropertyImages, UUID> {

    void deleteAllByProperty_Id(UUID propertyId);


}
