package com.example.immovision.controllers.user;

import com.example.immovision.dto.RegisterUserDTO;
import com.example.immovision.dto.UpdateUserDTO;
import com.example.immovision.dto.UserInfoDTO;
import com.example.immovision.entities.user.User;
import com.example.immovision.services.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<User> registerUser(@Valid @RequestBody RegisterUserDTO userDTO) {
        try {
            User newUser = userService.registerUser(userDTO);


            return ResponseEntity.status(HttpStatus.CREATED).body(newUser);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(null);
        }
    }


    @GetMapping("/agents")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getUserEmails() {
        try {
            var agents = userService.getUserEmails();


            return ResponseEntity.status(HttpStatus.OK).body(agents);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(null);
        }
    }

    @GetMapping("/info")
    public ResponseEntity<UserInfoDTO> getUserInfo(@RequestParam String email) {
        try {
            UserInfoDTO user = userService.getUserInfo(email);
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/update")
    public ResponseEntity<?> updateUser(@Valid @RequestBody UpdateUserDTO updateUserDTO) {
        try {
            UserInfoDTO updatedUser = userService.updateUser(updateUserDTO);
            return ResponseEntity.ok(updatedUser);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }


}
