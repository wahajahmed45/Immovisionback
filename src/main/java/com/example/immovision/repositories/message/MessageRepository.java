package com.example.immovision.repositories.message;

import com.example.immovision.entities.message.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface MessageRepository extends JpaRepository<Message, UUID> {

    /**
     * Trouve tous les messages d'une conversation entre deux utilisateurs pour une propriété spécifique.
     * Les messages sont triés par date d'envoi (du plus ancien au plus récent).
     *
     * @param user1Email Email du premier utilisateur
     * @param user2Email Email du deuxième utilisateur
     * @param propertyId ID de la propriété
     * @return Liste des messages de la conversation
     */
    @Query("""
        SELECT m FROM Message m 
        WHERE ((m.senderEmail = :user1Email AND m.receiverEmail = :user2Email)
        OR (m.senderEmail = :user2Email AND m.receiverEmail = :user1Email))
        AND m.property.id = :propertyId
        ORDER BY m.sentAt ASC
    """)
    List<Message> findByConversationAndProperty(
        @Param("user1Email") String user1Email,
        @Param("user2Email") String user2Email,
        @Param("propertyId") UUID propertyId
    );

    /**
     * Trouve le dernier message de chaque conversation pour un utilisateur.
     * Cette requête est utilisée pour afficher la liste des conversations dans la messagerie.
     * Les résultats sont triés avec :
     * 1. Les messages non lus en premier
     * 2. Par date d'envoi décroissante (du plus récent au plus ancien)
     *
     * @param email Email de l'utilisateur
     * @return Liste des derniers messages de chaque conversation
     */
    @Query("""
    SELECT m FROM Message m 
    WHERE m.sentAt IN (
        SELECT MAX(m2.sentAt) 
        FROM Message m2 
        WHERE m2.senderEmail = :email OR m2.receiverEmail = :email 
        GROUP BY 
            CASE 
                WHEN m2.senderEmail = :email THEN m2.receiverEmail 
                ELSE m2.senderEmail 
            END,
            m2.property
    )
    ORDER BY 
        CASE WHEN m.isRead = false AND m.receiverEmail = :email THEN 0 ELSE 1 END,
        m.sentAt DESC
    """)
    List<Message> findLastMessagesByUserAndProperty(@Param("email") String email);

    /**
     * Marque comme lus tous les messages non lus d'une conversation.
     * Cette méthode est appelée quand un utilisateur ouvre une conversation.
     *
     * @param receiverEmail Email du destinataire (celui qui lit les messages)
     * @param senderEmail Email de l'expéditeur
     * @param propertyId ID de la propriété
     */
    @Modifying
    @Query("""
        UPDATE Message m 
        SET m.isRead = true 
        WHERE m.receiverEmail = :receiverEmail 
        AND m.senderEmail = :senderEmail 
        AND m.property.id = :propertyId 
        AND m.isRead = false
    """)
    void markMessagesAsRead(
        @Param("receiverEmail") String receiverEmail,
        @Param("senderEmail") String senderEmail,
        @Param("propertyId") UUID propertyId
    );

    void deleteAllByProperty_Id(UUID propertyId);

}