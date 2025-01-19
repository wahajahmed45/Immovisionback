package com.example.immovision.services;

import com.example.immovision.dto.ReviewDTO;
import com.example.immovision.entities.review.Review;
import com.example.immovision.entities.user.User;
import com.example.immovision.repositories.property.PropertyRepository;
import com.example.immovision.repositories.review.ReviewRepository;
import com.example.immovision.repositories.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final PropertyRepository propertyRepository;

    /**
     * Récupère toutes les reviews d'une propriété spécifique.
     * Les reviews sont triées par date, de la plus récente à la plus ancienne.
     *
     * @param propertyId ID de la propriété
     * @return Liste des reviews converties en DTO
     */
    public List<ReviewDTO> getReviewsByPropertyId(UUID propertyId) {
        return reviewRepository.findByProperty_IdOrderByReviewDateDesc(propertyId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Calcule la note moyenne d'une propriété.
     * Si aucune review n'existe, retourne 0.
     *
     * @param propertyId ID de la propriété
     * @return Note moyenne de la propriété
     */
    public Double getPropertyAverageRating(UUID propertyId) {
        List<Review> reviews = reviewRepository.findByProperty_IdOrderByReviewDateDesc(propertyId);
        if (reviews.isEmpty()) {
            return 0.0;
        }
        return reviews.stream()
                .mapToDouble(Review::getPropertyRating)
                .average()
                .orElse(0.0);
    }

    /**
     * Calcule la note moyenne globale d'un agent.
     * Si aucune review n'existe, retourne 0.
     *
     * @param email Email de l'agent
     * @return Note moyenne de l'agent
     */
    public Double getAgentOverallRating(String email) {
        User user = userRepository.findByEmail(email).get();
        List<Review> reviews = reviewRepository.findByAgent_Id(user.getId());
        if (reviews.isEmpty()) {
            return 0.0;
        }
        return reviews.stream()
                .mapToDouble(Review::getAgentRating)
                .average()
                .orElse(0.0);
    }

    /**
     * Crée une nouvelle review.
     * Vérifie toutes les données requises et les relations entre entités.
     *
     * @param reviewDTO Données de la review à créer
     * @return Review créée convertie en DTO
     * @throws IllegalArgumentException si des données requises sont manquantes
     * @throws RuntimeException         si une entité référencée n'existe pas
     */
    @Transactional
    public ReviewDTO createReview(ReviewDTO reviewDTO) {
        // Validation des données requises
        if (reviewDTO.getPropertyId() == null) {
            throw new IllegalArgumentException("Property ID cannot be null");
        }
        if (reviewDTO.getAgentEmail() == null) {
            throw new IllegalArgumentException("Agent Email cannot be null");
        }
        if (reviewDTO.getUserEmail() == null) {
            throw new IllegalArgumentException("User Email cannot be null");
        }

        Review review = new Review();

        // Récupération et validation de la propriété
        var propertyOptional = propertyRepository.findById(reviewDTO.getPropertyId());
        if (propertyOptional.isEmpty()) {
            throw new RuntimeException("Property not found with id: " + reviewDTO.getPropertyId());
        }
        var property = propertyOptional.get();

        // Récupération et validation de l'agent
        var agentOptional = userRepository.findByEmail(reviewDTO.getAgentEmail());
        if (agentOptional.isEmpty()) {
            throw new RuntimeException("Agent not found with id: " + reviewDTO.getAgentEmail());
        }
        var agent = agentOptional.get();

        // Récupération et validation de l'utilisateur
        var userOptional = userRepository.findByEmail(reviewDTO.getUserEmail());
        if (userOptional.isEmpty()) {
            throw new RuntimeException("User not found with id: " + reviewDTO.getUserEmail());
        }
        var user = userOptional.get();

        // Configuration de la review
        review.setProperty(property);
        review.setAgent(agent);
        review.setUser(user);
        review.setPropertyRating(reviewDTO.getPropertyRating());
        review.setAgentRating(reviewDTO.getAgentRating());
        review.setReviewDate(LocalDateTime.now());
        review.setComment(reviewDTO.getComment());

        // Sauvegarde et conversion en DTO
        Review savedReview = reviewRepository.save(review);
        return convertToDTO(savedReview);
    }

    /**
     * Convertit une entité Review en ReviewDTO.
     * Cette méthode est utilisée pour transformer les données avant de les envoyer au front-end.
     *
     * @param review Review à convertir
     * @return ReviewDTO
     */
    private ReviewDTO convertToDTO(Review review) {
        return new ReviewDTO(
                review.getId(),
                review.getProperty().getId(),
                review.getAgent().getEmail(),
                review.getUser().getEmail(),
                review.getUser().getName(),
                review.getPropertyRating(),
                review.getAgentRating(),
                review.getReviewDate(),
                review.getComment()
        );
    }

    /**
     * Compte le nombre total de reviews pour un agent.
     *
     * @param email Email de l'agent
     * @return Nombre de reviews
     */
    public Long getAgentReviewCount(String email) {
        User user = userRepository.findByEmail(email).get();
        return reviewRepository.countByAgent_Id(user.getId());
    }
}
