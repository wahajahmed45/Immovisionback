package com.example.immovision.repositories.review;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.immovision.entities.review.Review;

/**
 * Repository pour gérer les avis sur les propriétés et les agents.
 * Gère les opérations CRUD pour l'entité Review.
 */
@Repository
public interface ReviewRepository extends JpaRepository<Review, UUID> {
    
    /**
     * Trouve tous les avis pour une propriété, triés par date décroissante.
     *
     * @param propertyId ID de la propriété
     * @return Liste des avis sur la propriété
     */
    List<Review> findByProperty_IdOrderByReviewDateDesc(UUID propertyId);

    /**
     * Trouve tous les avis reçus par un agent.
     *
     * @param agentId ID de l'agent
     * @return Liste des avis sur l'agent
     */
    List<Review> findByAgent_Id(UUID agentId);

    /**
     * Compte le nombre total d'avis reçus par un agent.
     *
     * @param agentId ID de l'agent
     * @return Nombre total d'avis
     */
    Long countByAgent_Id(UUID agentId);

    void deleteAllByProperty_Id(UUID propertyId);

}
