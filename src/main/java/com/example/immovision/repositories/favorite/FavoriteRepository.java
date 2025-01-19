package com.example.immovision.repositories.favorite;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.immovision.entities.favorite.Favorite;
import com.example.immovision.entities.property.Property;

/**
 * Repository pour gérer les favoris des utilisateurs.
 * Gère les opérations CRUD pour l'entité Favorite.
 */
public interface FavoriteRepository extends JpaRepository<Favorite, UUID> {

    /**
     * Trouve toutes les propriétés favorites d'un utilisateur.
     *
     * @param email Email de l'utilisateur
     * @return Liste des propriétés en favori
     */
    List<Property> findByUser_Email(String email);

    /**
     * Trouve un favori spécifique par propriété et utilisateur.
     *
     * @param propertyId ID de la propriété
     * @param email Email de l'utilisateur
     * @return Le favori s'il existe
     */
    Optional<Favorite> findByPropertyIdAndUserEmail(UUID propertyId, String email);

    /**
     * Vérifie si une propriété est déjà en favori pour un utilisateur.
     *
     * @param propertyId ID de la propriété
     * @param email Email de l'utilisateur
     * @return true si le favori existe, false sinon
     */
    boolean existsByPropertyIdAndUserEmail(UUID propertyId, String email);

    void deleteAllByProperty_Id(UUID propertyId);

}
