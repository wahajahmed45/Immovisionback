package com.example.immovision.repositories.user;

import com.example.immovision.entities.user.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface RoleRepository extends JpaRepository<Role, UUID> {
    Role findByName(String roleName);
}
