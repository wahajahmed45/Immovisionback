package com.example.immovision.services;

import com.example.immovision.entities.RefreshToken;
import com.example.immovision.entities.user.User;
import com.example.immovision.repositories.RefreshTokenRepository;
import com.example.immovision.repositories.user.UserRepository;
import com.example.immovision.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class RefreshTokenService {
    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserDetailsService userDetailsService;

    @Transactional
    public String createRefreshToken(String username) {
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new RuntimeException("User not found"));


        String refreshToken = UUID.randomUUID().toString();

        // Create and save refresh token
        RefreshToken token = new RefreshToken(user, refreshToken);
        refreshTokenRepository.save(token);

        return refreshToken;
    }

    @Transactional
    public String refreshAccessToken(String refreshToken) {

        RefreshToken storedToken = refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> new RuntimeException("Invalid refresh token"));


        if (!storedToken.isValid()) {
            refreshTokenRepository.delete(storedToken);
            throw new RuntimeException("Refresh token expired");
        }


        return jwtUtil.generateToken(storedToken.getUser().getEmail());
    }

    @Transactional
    public void revokeRefreshToken(String refreshToken) {
        RefreshToken token = refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> new RuntimeException("Token not found"));

        token.setRevoked(true);
        refreshTokenRepository.save(token);
    }
}