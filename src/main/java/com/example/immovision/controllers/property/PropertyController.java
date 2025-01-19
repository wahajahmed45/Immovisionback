package com.example.immovision.controllers.property;

import com.example.immovision.dto.*;
import com.example.immovision.entities.property.Property;
import com.example.immovision.repositories.favorite.FavoriteRepository;
import com.example.immovision.services.FavoriteService;
import com.example.immovision.services.PropertyService;
import com.example.immovision.services.image.CloudinaryImageService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@Slf4j
@RequestMapping("/properties")
public class PropertyController {

    @Autowired
    private PropertyService propertyService;
    @Autowired
    private CloudinaryImageService cloudinaryImageService;
    @Autowired
    private FavoriteRepository favoriteRepository;
    @Autowired
    private FavoriteService favoriteService;


    @GetMapping("/featured")
    public List<FeaturedPropertyDTO> getFeaturedProperties() {
        return propertyService.getFeaturedProperties();
    }


    @GetMapping("/filter")
    public List<FeaturedPropertyDTO> filterProperties(PropertyFilter filters) {
        return propertyService.findFilteredProperties(filters);

    }

    @GetMapping("/cities")

    public ResponseEntity<?> getPropertiesByCities() {
        var properties = propertyService.getPropertiesByCity();
        if (properties != null) {
            return new ResponseEntity<>(properties, HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Properties not found", HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/pending-properties")
    //@RolesAllowed("ROLE_AGENT")
    public List<PropertyDashboardDTO> getPendingProperties() {
        return propertyService.getPendingProperties();
    }

    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<PropertyDTO> createProperty(
            @RequestParam("data") String propertyDataJson,
            @RequestParam("images") MultipartFile[] images) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
            CreatePropertyDTO dto = mapper.readValue(propertyDataJson, CreatePropertyDTO.class);
            dto.setImages(images);

            Property property = propertyService.createProperty(dto);
            PropertyDTO responseDTO = propertyService.convertToDTO(property);
            return ResponseEntity.ok(responseDTO);
        } catch (Exception e) {
            log.error("Error creating property: ", e);
            throw new RuntimeException("Failed to create property", e);
        }
    }


    // Endpoint to get a property by its ID along with images
    @GetMapping("/{propertyId}")
    public ResponseEntity<?> getProperty(@PathVariable UUID propertyId) {
        PropertyDTO property = propertyService.getPropertyById(propertyId);

        if (property != null) {
            return new ResponseEntity<>(property, HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Property not found", HttpStatus.NOT_FOUND);
        }
    }

/*    // Endpoint to update property and its image
    @PutMapping("/{propertyId}/update")
    public ResponseEntity<?> updateProperty(@PathVariable UUID propertyId,
                                            @RequestPart("property") Property property,
                                            @RequestParam(value = "file", required = false) MultipartFile file) {
        try {
            Property existingProperty = propertyService.getPropertyById(propertyId);
            if (existingProperty == null) {
                return new ResponseEntity<>("Property not found", HttpStatus.NOT_FOUND);
            }

            // If a new file is uploaded, store it in Cloudinary and update image URL
            if (file != null && !file.isEmpty()) {
                String imageUrl = cloudinaryImageService.uploadImage(file);

                // Create or update the property image
                PropertyImages propertyImage = new PropertyImages();
                propertyImage.setImage_url(imageUrl);
                property.addPropertyImage(propertyImage);

                // Save the updated property image
                propertyService.savePropertyImage(propertyImage);
            }

            // Update the property
            existingProperty.setName(property.getName());
            existingProperty.setPrice(property.getPrice());
            // Add more fields here as needed

            Property updatedProperty = propertyService.saveProperty(existingProperty);
            return new ResponseEntity<>(updatedProperty, HttpStatus.OK);
        } catch (IOException e) {
            return new ResponseEntity<>("Failed to upload image", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }*/


    @PostMapping
    public ResponseEntity<Property> addProperty(@RequestBody Property property) {
        Property newProperty = propertyService.addProperty(property);
        return ResponseEntity.status(HttpStatus.CREATED).body(newProperty);
    }

    @PutMapping(value = "update/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<PropertyDTO> updateProperty(
            @RequestParam("data") String propertyDataJson,
            @RequestParam(value = "images", required = false) MultipartFile[] images) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

            UpdatePropertyDTO dto = mapper.readValue(propertyDataJson, UpdatePropertyDTO.class);
            if (images != null) {
                dto.setImages(images);
            }

            Property updatedProperty = propertyService.updateProperty(dto);
            PropertyDTO responseDTO = propertyService.convertToDTO(updatedProperty);
            return ResponseEntity.ok(responseDTO);
        } catch (Exception e) {
            log.error("Error updating property: ", e);
            throw new RuntimeException("Failed to update property", e);
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deletePropertyWithRelations(@PathVariable UUID id) {
        try {
            propertyService.deletePropertyWithRelations(id);
            return ResponseEntity.ok().body("Property and all related data successfully deleted");
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Property not found: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error deleting property: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProperty(@PathVariable UUID id) {
        propertyService.deleteProperty(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/properties")
    public Page<Property> searchProperties(
            @RequestParam Map<String, String> filters,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return propertyService.searchProperties(filters, page, size);
    }


    @PutMapping("approve/{propertyId}")
    public ResponseEntity<?> approveProperty(@PathVariable UUID propertyId, @RequestParam Boolean approved, @RequestParam String comment, @RequestParam String agentEmail) {
        var res = propertyService.updateApprovation(propertyId, approved, comment, agentEmail);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }


    // Add the following method to the PropertyController class in `src/main/java/com/example/immovision/controllers/property/PropertyController.java`

    @GetMapping("/favorite/get/{email}")
    public ResponseEntity<List<PropertyDashboardDTO>> getUserFavoriteProperty(@PathVariable String email) {
        List<PropertyDashboardDTO> dto = favoriteService.getAllFavoritesOfMyUser(email);
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @PostMapping("/favorite/add/{propertyId}")
    public ResponseEntity<?> addPropertyToFavorite(@PathVariable UUID propertyId, @RequestParam String email) {
        favoriteService.addFavorite(propertyId, email);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/favorite/remove/{propertyId}")
    public ResponseEntity<Void> removeFavorite(@PathVariable UUID propertyId, @RequestParam String email) {
        favoriteService.removeFavorite(propertyId, email);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/favorite/check/{propertyId}")
    public ResponseEntity<Boolean> checkFavorite(@PathVariable UUID propertyId, @RequestParam String email) {
        boolean isFavorite = favoriteService.isAlreadyAdded(propertyId, email);
        return ResponseEntity.ok(isFavorite);
    }

    @GetMapping("/user/{email}")
    public ResponseEntity<List<PropertyDashboardDTO>> getUserProperties(@PathVariable String email) {
        try {
            List<PropertyDashboardDTO> properties = propertyService.getUserProperties(email);
            return ResponseEntity.ok(properties);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/agent/{email}")
    public ResponseEntity<List<PropertyDashboardDTO>> getAgentProperties(@PathVariable String email) {
        try {
            List<PropertyDashboardDTO> properties = propertyService.getAgentProperties(email);
            return ResponseEntity.ok(properties);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    @DeleteMapping("/user/{email}/property/{propertyId}")
    public ResponseEntity<?> deleteUserProperty(@PathVariable String email, @PathVariable UUID propertyId) {
        try {
            boolean deleted = propertyService.deleteUserProperty(email, propertyId);
            if (deleted) {
                return ResponseEntity.ok().build();
            }
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You are not authorized to delete this property");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/all/cities")
    public ResponseEntity<?> getCities() {
        var properties = propertyService.getCities();
        if (properties != null) {
            return new ResponseEntity<>(properties, HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Cities not found", HttpStatus.NOT_FOUND);
        }

    }
}