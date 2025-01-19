package com.example.immovision.repositories;

import com.example.immovision.entities.RefreshToken;
import com.example.immovision.entities.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {
    Optional<RefreshToken> findByToken(String token);

    @Transactional
    void deleteByUser(User user);

    List<RefreshToken> findByUserAndRevokedFalse(User user);
}