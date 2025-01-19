package com.example.immovision.controllers.message;

import com.example.immovision.dto.ConversationDTO;
import com.example.immovision.dto.MessageDTO;
import com.example.immovision.entities.message.Message;
import com.example.immovision.services.MessageService;
import com.example.immovision.dto.SendMessageDTO;
import com.example.immovision.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/messages")
public class MessageController {

    @Autowired
    private MessageService messageService;

    @Autowired
    private UserService userService;

    // Envoyer un nouveau message
    @PostMapping("/send")
    public ResponseEntity<Message> sendMessage(@RequestBody SendMessageDTO messageDTO) {
        return ResponseEntity.ok(messageService.sendMessage(messageDTO));
    }

   // Obtenir la liste des conversations d'un utilisateur
   @GetMapping("/conversations/{userEmail}")
   public ResponseEntity<List<ConversationDTO>> getUserConversations(
       @PathVariable String userEmail
   ) {
       return ResponseEntity.ok(messageService.getUserConversations(userEmail));
   }

    // Obtenir tous les messages d'une conversation spécifique
    @GetMapping("/conversation")
    public ResponseEntity<List<MessageDTO>> getConversationMessages(
        @RequestParam String user1Email,
        @RequestParam String user2Email,
        @RequestParam String propertyId
    ) {
        return ResponseEntity.ok(
            messageService.getConversationMessages(user1Email, user2Email, propertyId)
        );
    }

    @PutMapping("/read")
public ResponseEntity<Void> markMessagesAsRead(
    @RequestParam String receiverEmail,
    @RequestParam String senderEmail,
    @RequestParam String propertyId
) {
    messageService.markMessagesAsRead(receiverEmail, senderEmail, propertyId);
    return ResponseEntity.ok().build();
}


}
