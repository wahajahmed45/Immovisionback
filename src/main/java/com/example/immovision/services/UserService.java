package com.example.immovision.services;

import com.example.immovision.dto.RegisterUserDTO;
import com.example.immovision.dto.UpdateUserDTO;
import com.example.immovision.dto.UserInfoDTO;
import com.example.immovision.entities.user.Role;
import com.example.immovision.entities.user.User;
import com.example.immovision.repositories.user.RoleRepository;
import com.example.immovision.repositories.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired

    private RoleRepository roleRepository;

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
        var pass = passwordEncoder.encode("1234");
        newUser.setPassword(pass);


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

}