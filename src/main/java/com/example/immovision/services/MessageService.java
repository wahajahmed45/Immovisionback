package com.example.immovision.services;

import com.example.immovision.dto.ConversationDTO;
import com.example.immovision.dto.MessageDTO;
import com.example.immovision.dto.SendMessageDTO;
import com.example.immovision.entities.message.Message;
import com.example.immovision.entities.property.Property;
import com.example.immovision.entities.user.User;
import com.example.immovision.repositories.message.MessageRepository;
import com.example.immovision.repositories.property.PropertyRepository;
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
public class MessageService {

    private final MessageRepository messageRepository;
    private final PropertyRepository propertyRepository;
    private final UserRepository userRepository;

    /**
     * Récupère toutes les conversations d'un utilisateur.
     * Pour chaque propriété, retourne le dernier message échangé.
     *
     * @param userEmail Email de l'utilisateur
     * @return Liste des conversations avec leur dernier message
     */
    public List<ConversationDTO> getUserConversations(String userEmail) {
        // Récupère le dernier message de chaque conversation
        List<Message> messages = messageRepository.findLastMessagesByUserAndProperty(userEmail);
        List<ConversationDTO> conversations = messages.stream()
                .map(message -> convertToConversationDTO(message, userEmail))
                .collect(Collectors.toList());
        return conversations;
    }

    /**
     * Récupère tous les messages d'une conversation entre deux utilisateurs pour une propriété donnée.
     *
     * @param user1Email Email du premier utilisateur
     * @param user2Email Email du deuxième utilisateur
     * @param propertyId ID de la propriété
     * @return Liste des messages de la conversation
     */
    public List<MessageDTO> getConversationMessages(String user1Email, String user2Email, String propertyId) {
        UUID propertyUUID = UUID.fromString(propertyId);
        List<Message> messages = messageRepository.findByConversationAndProperty(user1Email, user2Email, propertyUUID);
        return messages.stream()
                .map(this::convertToMessageDTO)
                .collect(Collectors.toList());
    }

    /**
     * Marque comme lus tous les messages d'une conversation.
     *
     * @param receiverEmail Email du destinataire
     * @param senderEmail   Email de l'expéditeur
     * @param propertyId    ID de la propriété
     */
    @Transactional
    public void markMessagesAsRead(String receiverEmail, String senderEmail, String propertyId) {
        UUID propertyUUID = UUID.fromString(propertyId);
        messageRepository.markMessagesAsRead(receiverEmail, senderEmail, propertyUUID);
    }

    /**
     * Convertit un Message en MessageDTO.
     * Inclut les informations basiques du message et les infos de la propriété.
     */
    private MessageDTO convertToMessageDTO(Message message) {
        MessageDTO dto = new MessageDTO();
        dto.setId(message.getId());
        dto.setContent(message.getContent());
        dto.setSenderEmail(message.getSenderEmail());
        dto.setReceiverEmail(message.getReceiverEmail());
        dto.setSentAt(message.getSentAt());
        dto.setRead(message.isRead());

        // Ajoute les informations de la propriété si elle existe
        if (message.getProperty() != null) {
            MessageDTO.PropertyInfoDTO propertyInfo = new MessageDTO.PropertyInfoDTO();
            propertyInfo.setId(message.getProperty().getId());
            propertyInfo.setTitle(message.getProperty().getTitle());
            dto.setProperty(propertyInfo);
        }

        return dto;
    }

    /**
     * Convertit un Message en ConversationDTO.
     * Crée un identifiant unique et cohérent pour la conversation basé sur les participants et la propriété.
     *
     * @param message   Message à convertir
     * @param userEmail Email de l'utilisateur actuel pour déterminer l'autre participant
     */
    private ConversationDTO convertToConversationDTO(Message message, String userEmail) {
        ConversationDTO dto = new ConversationDTO();

        // IMPORTANT: Génération d'un ID cohérent pour la conversation
        // Le bug précédent venait du fait que l'ID changeait selon qui envoyait le message
        // Solution: On utilise compareTo pour toujours avoir les emails dans le même ordre
        // Exemple: si on a une conversation entre bob@mail.com et alice@mail.com pour la propriété 123
        // L'ID sera toujours le même peu importe qui envoie le message car alice < bob
        String conversationKey = message.getSenderEmail().compareTo(message.getReceiverEmail()) < 0
                ? message.getSenderEmail() + "_" + message.getReceiverEmail() + "_" + message.getProperty().getId()
                : message.getReceiverEmail() + "_" + message.getSenderEmail() + "_" + message.getProperty().getId();

        dto.setId(UUID.nameUUIDFromBytes(conversationKey.getBytes()));

        // Configuration des informations du participant
        ConversationDTO.ParticipantDTO participant = new ConversationDTO.ParticipantDTO();

        // Détermine l'autre participant (si je suis le receiver, prendre le sender et vice versa)
        String otherUserEmail = message.getSenderEmail().equals(userEmail)
                ? message.getReceiverEmail()
                : message.getSenderEmail();

        participant.setEmail(otherUserEmail);

        // Récupération du nom de l'autre participant
        String userName = userRepository.findByEmail(otherUserEmail)
                .map(User::getName)
                .orElse("Anonym");
        participant.setName(userName);

        // Ajout des informations de rôle
        String role = determineParticipantRole(otherUserEmail, message.getProperty());
        participant.setRole(role);

        dto.setParticipant(participant);

        // Configuration des informations du dernier message
        ConversationDTO.LastMessageDTO lastMessage = new ConversationDTO.LastMessageDTO();
        lastMessage.setSenderEmail(message.getSenderEmail());
        lastMessage.setContent(message.getContent());
        lastMessage.setSentAt(message.getSentAt());
        lastMessage.setRead(message.isRead());
        dto.setLastMessage(lastMessage);

        // Configuration des informations de la propriété
        if (message.getProperty() != null) {
            ConversationDTO.PropertyInfoDTO propertyInfo = new ConversationDTO.PropertyInfoDTO();
            propertyInfo.setId(message.getProperty().getId());
            propertyInfo.setTitle(message.getProperty().getTitle());
            propertyInfo.setImageUrl(message.getProperty().getImages().get(0).getImage_url());
            propertyInfo.setOwnerEmail(message.getProperty().getOwner().getEmail());
            propertyInfo.setAgentEmail(message.getProperty().getAgent().getEmail());
            dto.setProperty(propertyInfo);
        }

        return dto;
    }

    /**
     * Envoie un nouveau message.
     *
     * @param messageDTO Données du message à envoyer
     * @return Le message créé
     */
    public Message sendMessage(SendMessageDTO messageDTO) {
        Property property = propertyRepository.findById(messageDTO.getPropertyId()).get();
        Message message = new Message();
        message.setProperty(property);
        message.setSentAt(LocalDateTime.now());
        message.setContent(messageDTO.getContent());
        message.setSenderEmail(messageDTO.getSenderEmail());
        message.setReceiverEmail(messageDTO.getReceiverEmail());

        return messageRepository.save(message);
    }

    /**
     * Détermine le rôle d'un participant dans une conversation.
     * Si le participant est agent, il sera toujours considéré comme AGENT même s'il est aussi propriétaire.
     *
     * @param participantEmail Email du participant
     * @param property         Propriété associée à la conversation
     * @return Le rôle du participant ("AGENT", "OWNER" ou "VISITOR")
     */
    private String determineParticipantRole(String participantEmail, Property property) {
        // Si c'est l'agent, on retourne AGENT même s'il est aussi propriétaire
        if (participantEmail.equals(property.getAgent().getEmail())) {
            return "AGENT";
        }
        // Sinon, on vérifie si c'est le propriétaire
        if (participantEmail.equals(property.getOwner().getEmail())) {
            return "OWNER";
        }
        // Si ni agent ni propriétaire, c'est un visiteur
        return "VISITOR";
    }

    /**
     * Envoie un message système pour les notifications de rendez-vous.
     *
     * @param content     Le contenu du message
     * @param agentEmail  Email de l'agent
     * @param clientEmail Email du client
     * @param propertyId  ID de la propriété
     * @param action      Type d'action (PENDING, APPROVED, REJECTED, CANCELLED)
     */
    @Transactional
    public void sendSystemMessage(String content, String agentEmail, String clientEmail, String propertyId, String action) {
        Message message = new Message();
        message.setContent(content);

        // Déterminer sender et receiver selon l'action
        switch (action) {
            case "PENDING", "CANCELLED":
                // L'agent est sender pour la proposition et l'annulation
                message.setSenderEmail(agentEmail);
                message.setReceiverEmail(clientEmail);
                break;
            case "APPROVED", "REJECTED":
                // Le client est sender pour l'acceptation et le refus
                message.setSenderEmail(clientEmail);
                message.setReceiverEmail(agentEmail);
                break;
            default:
                throw new IllegalArgumentException("Invalid action type: " + action);
        }

        message.setProperty(propertyRepository.findById(UUID.fromString(propertyId)).get());
        message.setSentAt(LocalDateTime.now());
        message.setRead(false);

        messageRepository.save(message);
    }

    /**
     * Envoie un message de notification quand une propriété est approuvée par un agent.
     * L'agent devient le sender et le propriétaire le receiver.
     *
     * @param property La propriété qui vient d'être approuvée
     */
    @Transactional
    public void sendPropertyApprovalMessage(Property property) {
        Message message = new Message();
        message.setContent(String.format(
                "Hi! I've reviewed and approved your property '%s'. It's now visible on our platform and ready for potential buyers. " +
                        "I'll be the dedicated agent for this property and will handle all inquiries. " +
                        "Feel free to contact me if you have any questions!",
                property.getTitle()
        ));

        message.setSenderEmail(property.getAgent().getEmail());
        message.setReceiverEmail(property.getOwner().getEmail());
        message.setProperty(property);
        message.setSentAt(LocalDateTime.now());
        message.setRead(false);

        messageRepository.save(message);
    }
}
