package com.example.immovision.entities;

import com.example.immovision.entities.user.User;
import jakarta.persistence.*;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
@Entity
@Table(name = "refresh_tokens")
public class RefreshToken {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String token;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private Instant expiryDate;

    @Column(nullable = false)
    private boolean revoked = false;


    public RefreshToken() {}

    public RefreshToken(User user, String token) {
        this.user = user;
        this.token = token;
        this.expiryDate = Instant.now().plusSeconds(7 * 24 * 60 * 60); // 7 days
    }
    public boolean isExpired() {
        return expiryDate.isBefore(Instant.now());
    }

    public boolean isValid() {
        return !isExpired() && !revoked;
    }

}