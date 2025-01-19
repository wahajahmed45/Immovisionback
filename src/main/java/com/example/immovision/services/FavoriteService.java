package com.example.immovision.services;

import com.example.immovision.dto.PropertyDashboardDTO;
import com.example.immovision.entities.favorite.Favorite;
import com.example.immovision.entities.property.Property;
import com.example.immovision.entities.user.User;
import com.example.immovision.repositories.favorite.FavoriteRepository;
import com.example.immovision.repositories.property.PropertyRepository;
import com.example.immovision.repositories.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FavoriteService {

    private final FavoriteRepository favoriteRepository;
    private final UserRepository userService;
    private final PropertyRepository propertyRepository;

    /**
     * Récupère tous les favoris d'un utilisateur.
     * Convertit chaque propriété favorite en DTO pour l'affichage dans le dashboard.
     *
     * @param email Email de l'utilisateur
     * @return Liste des propriétés favorites sous forme de DTO
     */
    public List<PropertyDashboardDTO> getAllFavoritesOfMyUser(String email) {

        return favoriteRepository.findAll().stream()
                .filter(favorite -> (favorite.getUser().getEmail()).equals(email))
                .map(favorite -> new PropertyDashboardDTO(
                        favorite.getProperty().getId(),
                        favorite.getProperty().getTitle(),
                        favorite.getProperty().getDescription(),
                        favorite.getProperty().getLocation(),
                        favorite.getProperty().getCity(),
                        favorite.getProperty().getState(),
                        favorite.getProperty().getCountry(),
                        favorite.getProperty().getZip(),
                        favorite.getProperty().getCreatedAt().toString(),
                        favorite.getProperty().getApprovalStatus().toString(),
                        favorite.getProperty().getUpdatedAt().toString(),
                        favorite.getProperty().getApprovationComment(),
                        favorite.getProperty().getImages().get(0).getImage_url(),
                        favorite.getProperty().getPrice(),
                        favorite.getProperty().getPriceLabel()
                ))
                .collect(Collectors.toList());
    }

    /**
     * Ajoute une propriété aux favoris d'un utilisateur.
     *
     * @param id    ID de la propriété à ajouter aux favoris
     * @param email Email de l'utilisateur
     * @return Le favori créé
     * @throws RuntimeException si l'utilisateur ou la propriété n'existe pas
     */
    public Favorite addFavorite(UUID id, String email) {
        User user = userService.findByEmail(email).get();
        Property property = propertyRepository.findById(id).get();

        Favorite favorite = new Favorite();
        favorite.setUser(user);
        favorite.setProperty(property);
        return favoriteRepository.save(favorite);
    }

    /**
     * Supprime une propriété des favoris d'un utilisateur.
     *
     * @param propertyId ID de la propriété à retirer des favoris
     * @param email      Email de l'utilisateur
     */
    public void removeFavorite(UUID propertyId, String email) {
        favoriteRepository.findByPropertyIdAndUserEmail(propertyId, email)
                .ifPresent(favoriteRepository::delete);
    }

    /**
     * Vérifie si une propriété est déjà dans les favoris d'un utilisateur.
     *
     * @param propertyId ID de la propriété à vérifier
     * @param email      Email de l'utilisateur
     * @return true si la propriété est déjà en favori, false sinon
     */
    public boolean isAlreadyAdded(UUID propertyId, String email) {
        return favoriteRepository.existsByPropertyIdAndUserEmail(propertyId, email);
    }
}
