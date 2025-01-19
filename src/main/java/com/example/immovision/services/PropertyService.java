package com.example.immovision.services;

import com.example.immovision.dto.*;
import com.example.immovision.entities.amenity.Amenity;
import com.example.immovision.entities.images.PropertyImages;
import com.example.immovision.entities.property.Property;
import com.example.immovision.entities.user.User;
import com.example.immovision.repositories.appointment.AppointmentRepository;
import com.example.immovision.repositories.favorite.FavoriteRepository;
import com.example.immovision.repositories.message.MessageRepository;
import com.example.immovision.repositories.property.PropertyRepository;
import com.example.immovision.repositories.propertyImage.PropertyImageRepository;
import com.example.immovision.repositories.review.ReviewRepository;
import com.example.immovision.repositories.user.UserRepository;
import com.example.immovision.services.image.CloudinaryImageService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service

@Slf4j
public class PropertyService {

    @Autowired
    private PropertyRepository propertyRepository;
    @Autowired
    private PropertyImageRepository propertyImageRepository;

    @Autowired
    private CloudinaryImageService cloudinaryService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GeocodingService geocodingService;

    @Autowired
    private MessageService messageService;

    @Autowired
private ReviewRepository reviewRepository;

@Autowired
private MessageRepository messageRepository;

@Autowired
private FavoriteRepository favoriteRepository;

@Autowired
private AppointmentRepository appointmentRepository;


    public List<String> getCities() {
        return propertyRepository.findAll().stream()
                .map(Property::getCity)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());
    }

   public List<FeaturedPropertyDTO> findFilteredProperties(PropertyFilter filter){
        List<Property> properties = propertyRepository.findFilteredProperties(
                filter.getPriceMin(),
                filter.getPriceMax(),
                filter.getRooms(),
                filter.getBathrooms(),
                filter.getCity()
        );

        return properties.stream()
                .map(property -> new FeaturedPropertyDTO(
                        property.getId() != null ? property.getId().toString() : null,
                        property.getType() != null ? property.getType() : "",
                        property.getLocation() != null ? property.getLocation() : "",
                        property.getCountry() != null ? property.getCountry() : "",
                        property.getCity() != null ? property.getCity() : "",
                        property.getZip() != null ? property.getZip() : "",
                        property.getPrice() != null ? property.getPrice() : 0,
                        property.getRoomCounts() != null ? property.getRoomCounts() : 0,
                        property.getIsAvailable() != null ? property.getIsAvailable() : false,
                        property.getIsForRent() != null ? property.getIsForRent() : false,
                        property.getTitle() != null ? property.getTitle() : "",
                        property.getLivingArea() != null ? property.getLivingArea() : 0,
                        property.getLandArea() != null ? property.getLandArea() : 0,
                        property.getDescription() != null ? property.getDescription() : "",
                        property.getStatus() != null ? property.getStatus() : "",
                        property.getPriceLabel() != null ? property.getPriceLabel() : "",
                        property.getBathroom() != null ? property.getBathroom() : 0,
                        property.getImages() != null ? property.getImages() : Collections.emptyList()
                ))
                .collect(Collectors.toList());

   }

    public List<FeaturedPropertyDTO> getFeaturedProperties() {
        return getProperties().stream()
                .map(property -> new FeaturedPropertyDTO(
                        property.getId() != null ? property.getId().toString() : null,
                        property.getType() != null ? property.getType() : "",
                        property.getLocation() != null ? property.getLocation() : "",
                        property.getCountry() != null ? property.getCountry() : "",
                        property.getCity() != null ? property.getCity() : "",
                        property.getZip() != null ? property.getZip() : "",
                        property.getPrice() != null ? property.getPrice() : 0,
                        property.getRoomCounts() != null ? property.getRoomCounts() : 0,
                        property.getIsAvailable() != null ? property.getIsAvailable() : false,
                        property.getIsForRent() != null ? property.getIsForRent() : false,
                        property.getTitle() != null ? property.getTitle() : "",
                        property.getLivingArea() != null ? property.getLivingArea() : 0,
                        property.getLandArea() != null ? property.getLandArea() : 0,
                        property.getDescription() != null ? property.getDescription() : "",
                        property.getStatus() != null ? property.getStatus() : "",
                        property.getPriceLabel() != null ? property.getPriceLabel() : "",
                        property.getBathroom() != null ? property.getBathroom() : 0,
                        property.getImages() != null ? property.getImages() : Collections.emptyList()
                ))
                .collect(Collectors.toList());
    }

    public List<PropertyDashboardDTO> getPendingProperties() {

        return propertyRepository.findAll().stream()
                .filter(property -> property.getApprovalStatus().equals(Property.ApprovalStatus.PENDING))
                .map(property -> new PropertyDashboardDTO(
                        property.getId(),
                        property.getTitle(),
                        property.getDescription(),
                        property.getLocation(),
                        property.getCity(),
                        property.getState(),
                        property.getCountry(),
                        property.getZip(),
                        property.getCreatedAt().toString(),
                        property.getApprovalStatus().toString(),
                        property.getUpdatedAt().toString(),
                        property.getApprovationComment(),
                        property.getImages().get(0).getImage_url(),
                        property.getPrice(),
                        property.getPriceLabel()
                ))
                .collect(Collectors.toList());
    }

    public Property createProperty(CreatePropertyDTO dto) {
        try {
            // Create new Property entity
            Property property = new Property();
            Optional<User> ownerUser = userRepository.findByEmail(dto.getOwnerEmail());
            if (!ownerUser.isPresent()) {
                throw new EntityNotFoundException("User not found with email: " + dto.getOwnerEmail());
            }

            User owner = ownerUser.get();
            if (owner.getRoles() != null && "agent".equals(owner.getRoles().getName())) {
                property.setAgent(owner);
                property.setApprovalStatus(Property.ApprovalStatus.APPROVED);
            } else {
                property.setApprovalStatus(Property.ApprovalStatus.PENDING);
            }

            property.setOwner(owner);

            // Map basic fields from DTO to entity
            property.setTitle(dto.getTitle());
            property.setDescription(dto.getDescription());
            property.setType(dto.getType());
            property.setStatus(dto.getStatus());
            property.setCategory(dto.getCategory());
            property.setPrice(dto.getPrice());
            property.setPriceLabel(dto.getPriceLabel());
            property.setPricePrefix(dto.getPricePrefix());

            // Map financial details
            property.setYearlyTaxRate(dto.getYearlyTaxRate());
            property.setHoaFee(dto.getHoaFee());

            // Map property details
            //property.setRoomCount(dto.getRoomCount());
            property.setBathroom(dto.getBathroom());
            property.setLivingArea(dto.getLivingArea());
            property.setLandArea(dto.getLandArea());
            property.setGarage(dto.getGarage());
            property.setGarageSize(dto.getGarageSize());
            property.setYearBuilt(dto.getYearBuilt());
            property.setAvailableFrom(dto.getAvailableFrom());
            property.setBasement(dto.getBasement());
            property.setExtraDetails(dto.getExtraDetails());
            property.setBedroom(dto.getBedroom());
            property.setToilet(dto.getToilet());
            property.setFloorNumber(dto.getFloorNumber());
            property.setBuildingCondition(dto.getBuildingCondition());
            property.setTypeOfEnvironnement(dto.getTypeOfEnvironnement());
            property.setAttic(dto.getAttic());
            property.setGarden(dto.getGarden());
            property.setGardenArea(dto.getGardenArea());
            property.setParking(dto.getParking());
            property.setRoomCounts(dto.getRoomCount());


            // Map location details
            property.setLocation(dto.getLocation());
            property.setCountry(dto.getCountry());
            property.setState(dto.getState());
            property.setCity(dto.getCity());
            property.setZip(dto.getZip());

            // Map media links
            property.setVirtualTour(dto.getVirtualTour());
            property.setPropertyVideo(dto.getPropertyVideo());
            property.setFloorPlan(dto.getFloorPlan());

            // Map status flags
            property.setIsAvailable(dto.getAvailable());
            property.setIsForRent(dto.getForRent());

            // Handle image upload
            if (dto.getImages() != null && dto.getImages().length > 0) {
                if (property.getImages() == null) {
                    property.setImages(new ArrayList<>());
                }

                for (MultipartFile imageFile : dto.getImages()) {
                    if (imageFile != null && !imageFile.isEmpty()) {
                        try {
                            Map<String, String> imageUrl = cloudinaryService.uploadImage(imageFile);

                            PropertyImages propertyImage = new PropertyImages();
                            propertyImage.setProperty(property);
                            propertyImage.setImage_url(imageUrl.get("secureUrl"));
                            property.getImages().add(propertyImage);

                        } catch (Exception e) {
                            log.error("Error uploading image to Cloudinary: ", e);
                            // Continue with other images even if one fails
                        }
                    }
                }
            }

            // Handle amenities
            Amenity amenities = new Amenity();
            amenities.setInterior(new ArrayList<>(dto.getAmenities().getInterior()));
            amenities.setExterior(new ArrayList<>(dto.getAmenities().getExterior()));
            amenities.setOther(new ArrayList<>(dto.getAmenities().getOther()));
            amenities.setProperty(property);
            property.setAmenities(amenities);
            property.setIsFeatured(false);
            property.setCreatedAt(LocalDateTime.now());

            String address = dto.getLocation() + ", " + dto.getZip() + ", " + dto.getCountry();

            try {
                GeocodingService.Coordinates coordinates = geocodingService.getCoordinates(address);
                property.setLatitude(coordinates.getLat());
                property.setLongitude(coordinates.getLng());
            } catch (Exception e) {
                property.setLatitude(0.0);
                property.setLongitude(0.0);

            }


            // Save and return the property
            return propertyRepository.save(property);

        } catch (Exception e) {
            log.error("Error creating property: ", e);
            throw new RuntimeException("Failed to create property", e);
        }
    }

    public PropertyDTO convertToDTO(Property property) {
        PropertyDTO dto = new PropertyDTO();

        dto.setPropertyId(property.getId());
        if (property.getAgent() != null) {
            dto.setAgentEmail(property.getAgent().getEmail());
            dto.setAgentName(property.getAgent().getName());
        } else {
            dto.setAgentEmail("");
            dto.setAgentName("");
        }


        // Map basic fields
        dto.setTitle(property.getTitle());
        dto.setDescription(property.getDescription());
        dto.setType(property.getType());
        dto.setStatus(property.getStatus());
        dto.setCategory(property.getCategory());
        dto.setPrice(property.getPrice());
        dto.setPriceLabel(property.getPriceLabel());
        dto.setPricePrefix(property.getPricePrefix());
        dto.setOwnerEmail(property.getOwner().getEmail());

        // Map financial details
        dto.setYearlyTaxRate(property.getYearlyTaxRate());
        dto.setHoaFee(property.getHoaFee());

        // Map property details
        dto.setRoomCounts(property.getRoomCounts());
        dto.setBathroom(property.getBathroom());
        dto.setLivingArea(property.getLivingArea());
        dto.setLandArea(property.getLandArea());
        dto.setGarage(property.getGarage());
        dto.setGarageSize(property.getGarageSize());
        dto.setYearBuilt(property.getYearBuilt());
        dto.setAvailableFrom(property.getAvailableFrom());
        dto.setBasement(property.getBasement());
        dto.setExtraDetails(property.getExtraDetails());
        dto.setBedroom(property.getBedroom());
        dto.setToilet(property.getToilet());
        dto.setFloorNumber(property.getFloorNumber());
        dto.setBuildingCondition(property.getBuildingCondition());
        dto.setTypeOfEnvironnement(property.getTypeOfEnvironnement());
        dto.setAttic(property.getAttic());
        dto.setGarden(property.getGarden());
        dto.setGardenArea(property.getGardenArea());
        dto.setUpdatedAt(property.getUpdatedAt());
        dto.setIsApproved(property.getIsApproved());
        dto.setApprovationStatus(property.getApprovalStatus().toString());
        dto.setApprovationComment(property.getApprovationComment());
        dto.setParking(property.getParking());


        // Map location details
        dto.setLocation(property.getLocation());
        dto.setCountry(property.getCountry());
        dto.setState(property.getState());
        dto.setCity(property.getCity());
        dto.setZip(property.getZip());

        // Map media links
        dto.setVirtualTour(property.getVirtualTour());
        dto.setPropertyVideo(property.getPropertyVideo());
        dto.setFloorPlan(property.getFloorPlan());
        if (property.getImages() != null && !property.getImages().isEmpty()) {
            dto.setImageUrl(
                property.getImages().stream()
                    .map(PropertyImages::getImage_url)
                    .toArray(String[]::new)
            );
        } else {
            dto.setImageUrl(new String[0]);
        }

        // Map status flags
        dto.setAvailable(property.getIsAvailable());
        dto.setForRent(property.getIsForRent());

        // Map amenities
        AmenitiesDTO amenitiesDTO = new AmenitiesDTO();
        if (property.getAmenities() != null) {
            amenitiesDTO.setInterior(property.getAmenities().getInterior());
            amenitiesDTO.setExterior(property.getAmenities().getExterior());
            amenitiesDTO.setOther(property.getAmenities().getOther());
        }
        dto.setAmenities(amenitiesDTO);

        return dto;
    }


    public List<Property> getProperties() {
        return propertyRepository.findAll();
    }

    public PropertyDTO getPropertyById(UUID id) {
        Optional<Property> property = propertyRepository.findById(id);
        return property.map(this::convertToDTO).orElse(null);
    }

    public PropertyImages savePropertyImage(PropertyImages propertyImage) {
        return propertyImageRepository.save(propertyImage);
    }


    public Property saveProperty(Property property) {
        return propertyRepository.save(property);
    }


    public Property addProperty(Property property) {
        return propertyRepository.save(property);
    }

//    public Property convertDTOToProperty(PropertyDTO propertyDTO) {
//        Property property = new Property();
//        Optional<User> user = userRepository.findByEmail(propertyDTO.getOwnerEmail());
//
//
//
//
//    }

    public Property updateApprovation(UUID id, Boolean approved, String comment, String agentEmail) {
        Property property = propertyRepository.findById(id).get();
        User agent = userRepository.findByEmail(agentEmail).get();

        if (approved) {
            property.setApprovalStatus(Property.ApprovalStatus.APPROVED);
            property.setAgent(agent);
            property.setApprovationComment(comment);
            messageService.sendPropertyApprovalMessage(property);
        } else {
            property.setApprovalStatus(Property.ApprovalStatus.REJECTED);
            property.setApprovationComment(comment);
        }
        return propertyRepository.save(property);
    }

    public Property updateProperty(UpdatePropertyDTO dto) {
        Property property = propertyRepository.findById(dto.getPropertyId())
            .orElseThrow(() -> new EntityNotFoundException("Property not found"));

        System.out.println("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA " + property.getRoomCounts() + " DTO : " + dto.getParking());


        // Supprimer les images marquées
        if (dto.getImagesToDelete() != null) {
            for (String imageUrl : dto.getImagesToDelete()) {
                cloudinaryService.deleteImage(imageUrl);
                property.getImages().removeIf(img -> img.getImage_url().equals(imageUrl));
            }
        }

        // Ajouter les nouvelles images
        if (dto.getImages() != null && dto.getImages().length > 0) {
            for (MultipartFile imageFile : dto.getImages()) {
                if (imageFile != null && !imageFile.isEmpty()) {
                    try {
                        Map<String, String> uploadResult = cloudinaryService.uploadImage(imageFile);
                        PropertyImages propertyImage = new PropertyImages();
                        propertyImage.setProperty(property);
                        propertyImage.setImage_url(uploadResult.get("secureUrl"));
                        property.getImages().add(propertyImage);
                    } catch (Exception e) {
                        log.error("Error uploading image to Cloudinary: ", e);
                    }
                }
            }
        }

        // Mettre à jour les amenities
        if (property.getAmenities() == null) {
            property.setAmenities(new Amenity());
            property.getAmenities().setProperty(property);
        }
        
        // Mettre à jour les listes d'amenities
        if (dto.getAmenities() != null) {
            property.getAmenities().setInterior(new ArrayList<>(dto.getAmenities().getInterior()));
            property.getAmenities().setExterior(new ArrayList<>(dto.getAmenities().getExterior()));
            property.getAmenities().setOther(new ArrayList<>(dto.getAmenities().getOther()));
        }

        // Copier tous les champs comme dans createProperty
        BeanUtils.copyProperties(dto, property, 
            "propertyId", "createdAt", "owner", "agent", "approvalStatus", "images", "imagesToDelete", "amenities");
        property.setUpdatedAt(LocalDateTime.now());
        //si mon bien est rejected alors on le mets en pending, car il a été modifier (pour le user)
        if(property.getApprovalStatus().equals(Property.ApprovalStatus.REJECTED)) {
            property.setApprovalStatus(Property.ApprovalStatus.PENDING);
        }

        //mettres a jours autre elements
        property.setRoomCounts(dto.getRoomCount());
        property.setParking(dto.getParking());

        return propertyRepository.save(property);
    }

    public void deleteProperty(UUID id) {
        propertyRepository.deleteById(id);
    }

    /**
     * Search for properties based on the provided filters
     *
     * @param filters Filters to apply to the search (price range, location, etc.)
     * @param page    Requested page number
     * @param size    Number of items per page
     * @return List of properties that match the search criteria
     */
    public Page<Property> searchProperties(final Map<String, String> filters, int page, int size) {
        Specification<Property> spec = Specification.where(null);

        for (Map.Entry<String, String> entry : filters.entrySet()) {
            spec = switch (entry.getKey()) {
                case "minPrice" ->
                        spec.and((root, query, cb) -> cb.ge(root.get("price"), Double.parseDouble(entry.getValue())));
                case "maxPrice" ->
                        spec.and((root, query, cb) -> cb.le(root.get("price"), Double.parseDouble(entry.getValue())));
                case "keyword" -> spec.and((root, query, cb) -> cb.or(
                                cb.like(root.get("title"), "%" + entry.getValue() + "%"),
                                cb.like(root.get("description"), "%" + entry.getValue() + "%")
                        ));
                case "propertyType" -> spec.and((root, query, cb) -> cb.equal(root.get("type"), entry.getValue()));
                case "amenities" -> {
                    String[] amenities = entry.getValue().split(",");
                    List<String> amenitiesList = Arrays.asList(amenities);
                    yield spec.and((root, query, cb) -> root.join("amenities").get("other").in(amenitiesList));
                }
                case "minBeds" ->
                        spec.and((root, query, cb) -> cb.ge(root.get("roomCount"), Integer.parseInt(entry.getValue())));
                case "minBathrooms" ->
                        spec.and((root, query, cb) -> cb.ge(root.get("bathroom"), Integer.parseInt(entry.getValue())));
                case "category" -> spec.and((root, query, cb) -> cb.equal(root.get("status"), entry.getValue()));
                case "location" ->
                        spec.and((root, query, cb) -> cb.like(root.get("location"), "%" + entry.getValue() + "%"));
                default -> spec;
            };
        }

        final Pageable pageable = PageRequest.of(page, size);
        return propertyRepository.findAll(spec, pageable);      // Page is used for performance improvements in case there is a lot of properties
    }


    public Map<String, List<PropertyByCityDTO>> getPropertiesByCity() {
        List<Property> properties = propertyRepository.findAll();

        return properties.stream()
                .map(this::convertToPropertyByCityDTO)
                .collect(Collectors.groupingBy(com.example.immovision.dto.Property::getCity));
    }

    private PropertyByCityDTO convertToPropertyByCityDTO(Property property) {
        PropertyByCityDTO dto = new PropertyByCityDTO();
        dto.setType(property.getType());
        dto.setLocation(property.getLocation());
        dto.setCountry(property.getCountry());
        dto.setCity(property.getCity());
        dto.setZip(property.getZip());
        dto.setPrice(property.getPrice());
        dto.setAvailable(property.getIsAvailable());
        dto.setForRent(property.getIsForRent());
        dto.setTitle(property.getTitle());
        dto.setLivingArea(property.getLivingArea());
        dto.setLandArea(property.getLandArea());
        dto.setDescription(property.getDescription());
        dto.setBathroom(property.getBathroom());
        dto.setImages(property.getImages());
        dto.setLatitude(property.getLatitude());
        dto.setLongitude(property.getLongitude());
        return dto;
    }

    public List<PropertyDashboardDTO> getUserProperties(String email) {
        return propertyRepository.findByOwner_Email(email).stream()
                .map(property -> new PropertyDashboardDTO(
                        property.getId(),
                        property.getTitle(),
                        property.getDescription(),
                        property.getLocation(),
                        property.getCity(),
                        property.getState(),
                        property.getCountry(),
                        property.getZip(),
                        property.getCreatedAt().toString(),
                        property.getApprovalStatus().toString(),
                        property.getApprovationComment(),
                        property.getUpdatedAt().toString(),
                        property.getImages().isEmpty() ? null : property.getImages().get(0).getImage_url(),
                        property.getPrice(),
                        property.getPriceLabel()
                ))
                .collect(Collectors.toList());
    }

    public List<PropertyDashboardDTO> getAgentProperties(String email) {
        return propertyRepository.findByAgent_Email(email).stream()
                .map(property -> new PropertyDashboardDTO(
                        property.getId(),
                        property.getTitle(),
                        property.getDescription(),
                        property.getLocation(),
                        property.getCity(),
                        property.getState(),
                        property.getCountry(),
                        property.getZip(),
                        property.getCreatedAt().toString(),
                        property.getApprovalStatus().toString(),
                        property.getApprovationComment(),
                        property.getUpdatedAt().toString(),
                        property.getImages().isEmpty() ? null : property.getImages().get(0).getImage_url(),
                        property.getPrice(),
                        property.getPriceLabel()
                ))
                .collect(Collectors.toList());
    }


    public boolean deleteUserProperty(String email, UUID propertyId) {
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new EntityNotFoundException("Property not found"));
                
        // Vérifier si l'utilisateur est le propriétaire
        if (property.getOwner() != null && property.getOwner().getEmail().equals(email)) {
            propertyRepository.deleteById(propertyId);
            return true;
        }
        return false;
    }

    @Transactional
    public void deletePropertyWithRelations(UUID id) {
        try {
            Property property = propertyRepository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("Property not found"));

            // 1. Supprimer les reviews
            reviewRepository.deleteAllByProperty_Id(id);

            // 2. Supprimer des favoris
            favoriteRepository.deleteAllByProperty_Id(id);

            // 3. Supprimer les rendez-vous
            appointmentRepository.deleteAllByProperty_Id(id);

            // 4. Supprimer les messages
            messageRepository.deleteAllByProperty_Id(id);

            // 5. Supprimer les images de Cloudinary
            if (property.getImages() != null) {
                for (PropertyImages image : property.getImages()) {
                    cloudinaryService.deleteImage(image.getImage_url());
                }
            }

            // 6. Supprimer les images de la bd
            propertyImageRepository.deleteAllByProperty_Id(id);


            // 7. Supprimer les amenities
            if (property.getAmenities() != null) {
                // Les tables de jointure seront automatiquement nettoyées grâce aux annotations @ElementCollection
                property.getAmenities().getInterior().clear();
                property.getAmenities().getExterior().clear();
                property.getAmenities().getOther().clear();
            }

            // 8. Enfin, supprimer la propriété
            propertyRepository.deleteById(id);
        } catch (Exception e) {
            log.error("Error deleting property with ID: {}", id, e);
            throw new RuntimeException("Failed to delete property and its relations", e);
        }
    }

    
}
