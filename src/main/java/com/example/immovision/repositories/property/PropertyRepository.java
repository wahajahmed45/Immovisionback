package com.example.immovision.repositories.property;

import com.example.immovision.entities.amenity.Amenity;
import com.example.immovision.entities.property.Property;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface PropertyRepository extends JpaRepository<Property, UUID>, JpaSpecificationExecutor<Property> {

   // List<Property> findByKeyword(String keyword);

  //  List<Property> findByLocation(String location);

   // List<Property> findByPriceBetween(double minPrice, double maxPrice);

   // List<Property> findByPropertyType(final String propertyType);

    //List<Property> findByAmenities(final Amenity amenities);

    //List<Property> findByBathroom(final int bathrooms);

    //List<Property> findByBedrooms(final int bedrooms);

    //List<Property> findByArea(final double area);

    //List<Property> findByStatus(final String status);

   // List<Property> findBySeller(final String seller);

    List<Property> findByOwner_Email(String email);

    List<Property> findByAgent_Email(String email);

    @Query("SELECT DISTINCT p FROM Property p LEFT JOIN p.amenities a WHERE " +
            "(:priceMin IS NULL OR p.price >= :priceMin) AND " +
            "(:priceMax IS NULL OR p.price <= :priceMax) AND " +
            "(:rooms IS NULL OR p.bedroom = :rooms) AND " +
            "(:bathrooms IS NULL OR p.bathroom = :bathrooms) AND " +
            "(:city IS NULL OR p.city = :city)")
    List<Property> findFilteredProperties(
            @Param("priceMin") Integer priceMin,
            @Param("priceMax") Integer priceMax,
            @Param("rooms") Integer rooms,
            @Param("bathrooms") Integer bathrooms,
            @Param("city") String city);
}
