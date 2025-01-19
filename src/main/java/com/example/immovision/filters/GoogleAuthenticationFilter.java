package com.example.immovision.filters;

import com.example.immovision.dto.GoogleTokenDto;
import com.example.immovision.entities.user.User;
import com.example.immovision.security.GoogleAuthenticationToken;
import com.example.immovision.services.RefreshTokenService;
import com.example.immovision.services.UserService;
import com.example.immovision.utils.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class GoogleAuthenticationFilter extends AbstractAuthenticationProcessingFilter {
    private static final NetHttpTransport TRANSPORT = new NetHttpTransport();
    private static final JacksonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

    private final ObjectMapper objectMapper;
    private final JwtUtil jwtUtil;
    private final UserService userService;
    private final RefreshTokenService refreshTokenService;

    public GoogleAuthenticationFilter(
            ObjectMapper objectMapper,
            JwtUtil jwtUtil,
            UserService userService,
            RefreshTokenService refreshTokenService) {
        super(new AntPathRequestMatcher("/api/auth/google-signin", "POST"));
        this.objectMapper = objectMapper;
        this.jwtUtil = jwtUtil;
        this.userService = userService;
        this.refreshTokenService = refreshTokenService;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException, IOException {
        try {
            GoogleTokenDto tokenDto = objectMapper.readValue(request.getInputStream(), GoogleTokenDto.class);

            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(TRANSPORT, JSON_FACTORY)
                    .setAudience(Collections.singletonList("391094031907-taklbls70hk48us3rolpipepiprfdjkl.apps.googleusercontent.com"))
                    .build();

            GoogleIdToken idToken = verifier.verify(tokenDto.getIdToken());

            if (idToken != null) {
                GoogleIdToken.Payload payload = idToken.getPayload();
                String email = payload.getEmail();
                String googleId = payload.getSubject();
                String name = (String) payload.get("name");

                User user = userService.findOrCreateUserByGoogle(email, googleId, name);
                return new GoogleAuthenticationToken(user);
            }

            throw new RuntimeException("Invalid Google token");

        } catch (Exception e) {
            throw new RuntimeException("Failed to process Google authentication", e);
        }
    }

    @Override
    protected void successfulAuthentication(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain chain,
            Authentication authResult) throws IOException, ServletException {

        User user = (User) authResult.getPrincipal();
        String accessToken = jwtUtil.generateToken(user.getEmail());
        String refreshToken = refreshTokenService.createRefreshToken(user.getEmail());

        List<String> acls = user.getRoles().getAcls().stream()
                .map(acl -> acl.getName())
                .distinct()
                .collect(Collectors.toList());

        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        objectMapper.writeValue(response.getWriter(), Map.of(
                "message", "Google authentication successful",
                "token", accessToken,
                "refreshToken", refreshToken,
                "acls", acls,
                "role", user.getRoles().getName(),
                "emailUser", user.getEmail()
        ));
    }

    @Override
    protected void unsuccessfulAuthentication(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException failed) throws IOException, ServletException {

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        objectMapper.writeValue(response.getWriter(), Map.of(
                "message", "Google authentication failed",
                "error", failed.getMessage()
        ));
    }
}