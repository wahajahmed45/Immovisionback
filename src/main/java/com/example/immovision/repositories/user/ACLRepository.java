package com.example.immovision.repositories.user;

import com.example.immovision.entities.user.ACL;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ACLRepository extends JpaRepository<ACL, UUID> {

}
