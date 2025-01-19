package com.example.immovision.services;

import com.example.immovision.dto.RegisterUserDTO;
import com.example.immovision.dto.UpdateUserDTO;
import com.example.immovision.dto.UserInfoDTO;
import com.example.immovision.entities.user.Role;
import com.example.immovision.entities.user.User;
import com.example.immovision.repositories.user.RoleRepository;
import com.example.immovision.repositories.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.mail.javamail.JavaMailSender;


import java.util.*;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
   // @Autowired
   // private JavaMailSender emailSender;


    @Autowired
    public UserService(PasswordEncoder passwordEncoder) {

        this.passwordEncoder = passwordEncoder;
    }

    private PasswordEncoder passwordEncoder;

    public User registerUser(RegisterUserDTO userDTO) {
        Role role = roleRepository.findByName("USER");

        User user = new User();
        user.setName(userDTO.getFullName());
        user.setEmail(userDTO.getEmail());
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        user.setRoles(role);


        return userRepository.save(user);
    }

    public List<String> getUserEmails() {
        List<String> emails = new ArrayList<>();
        List<User> users = userRepository.findAll();
        for (User user : users) {
            emails.add(user.getEmail());
        }
        return emails;

    }

    public User findOrCreateUserByGoogle(String email, String googleId, String name) {

        Optional<User> existingUser = userRepository.findByEmail(email);
        Role role = roleRepository.findByName("USER");

        if (existingUser.isPresent()) {

            User user = existingUser.get();
            user.setGoogleId(googleId);
            return userRepository.save(user);
        }


        User newUser = new User();
        newUser.setEmail(email);
        newUser.setName(name);
        newUser.setGoogleId(googleId);
        newUser.setRoles(role);


        return userRepository.save(newUser);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public User findById(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
    }

    public UserInfoDTO getUserInfo(String email) {
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isPresent()) {
            User user2 = user.get();
            return new UserInfoDTO(user2.getName(), user2.getEmail(), user2.getRoles().getName());
        }
        return null;
    }

    public boolean changePassword(String email, String currentPassword, String newPassword) {
        User user = findByEmail(email).orElseThrow(() -> new RuntimeException("User  not found"));


        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            return false;
        }

        // Update the password
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        return true;
    }

    public UserInfoDTO updateUser(UpdateUserDTO updateUserDTO) {
        User user = findByEmail(updateUserDTO.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Vérifier si le nouvel email est différent et s'il n'existe pas déjà
        if (!updateUserDTO.getNewEmail().equals(user.getEmail())) {
            if (userRepository.findByEmail(updateUserDTO.getNewEmail()).isPresent()) {
                throw new RuntimeException("Email already exists");
            }
            user.setEmail(updateUserDTO.getNewEmail());
        }

        // Mettre à jour le nom
        user.setName(updateUserDTO.getName());

        User updatedUser = userRepository.save(user);
        return new UserInfoDTO(updatedUser.getName(), updatedUser.getEmail(), updatedUser.getRoles().getName());
    }

//    public void forgotPassword(String email) {
//        try {
//            User user = findByEmail(email)
//                    .orElseThrow(() -> new RuntimeException("User not found"));
//
//            String temporaryPassword = generateRandomPassword();
//            user.setPassword(passwordEncoder.encode(temporaryPassword));
//            userRepository.save(user);
//
//            SimpleMailMessage message = new SimpleMailMessage();
//            message.setTo(user.getEmail());
//            message.setSubject("Password Reset - ImmoVision");
//            message.setText("Hello " + user.getName() + ",\n\n" +
//                    "Here is your temporary password: " + temporaryPassword + "\n\n" +
//                    "For security reasons, we recommend changing this password upon your next login.\n\n" +
//                    "Best regards,\n" +
//                    "ImmoVision Team");
//
//            try {
//                emailSender.send(message);
//            } catch (MailAuthenticationException e) {
//                throw new RuntimeException("Email service authentication failed. Please contact support.");
//            } catch (MailException e) {
//                throw new RuntimeException("Failed to send email. Please try again later.");
//            }
//        } catch (RuntimeException e) {
//            throw new RuntimeException("Password reset failed: " + e.getMessage());
//        }
//    }

    private String generateRandomPassword() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*";
        StringBuilder password = new StringBuilder();
        Random random = new Random();

        for (int i = 0; i < 12; i++) {
            password.append(chars.charAt(random.nextInt(chars.length())));
        }

        return password.toString();
    }


}